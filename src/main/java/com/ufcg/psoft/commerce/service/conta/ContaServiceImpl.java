package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.repository.ItemCarteiraRepository;;
import com.ufcg.psoft.commerce.listener.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.listener.NotificacaoAtivoVariouCotacao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContaServiceImpl implements ContaService {

    @Autowired
    ContaRepository contaRepository;

    @Autowired
    CompraRepository compraRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ItemCarteiraRepository itemCarteiraRepository;

    @Override
    public Conta criarContaPadrao() {
        Conta conta = Conta.builder()
                .saldo(BigDecimal.valueOf(0.00))
                .ativosDeInteresse(new ArrayList<>())
                .carteira(new ArrayList<>())
                .build();

        return contaRepository.save(conta);
    }

    @Override //provavelmente aqui é melhor implementar o metodo direto da interface notificacaoListener
    public void notificarAtivoDisponivelClientesComInteresse(EventoAtivo evento) {
        NotificacaoAtivoDisponivel notificacao = new NotificacaoAtivoDisponivel();
        notificacao.notificarAtivoDisponivel(evento);
    }

    @Override //provavelmente aqui é melhor implementar o metodo direto da interface notificacaoListener
    public void notificarClientesPremiumComInteresse(EventoAtivo evento){
        NotificacaoAtivoVariouCotacao notificacao = new NotificacaoAtivoVariouCotacao();
        notificacao.notificarAtivoVariouCotacao(evento);
    }

    @Override
    public CompraResponseDTO confirmarCompra(Long idCliente, Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.DISPONIVEL) {
            throw new StatusCompraInvalidoException();
        }

        Conta conta = compra.getCliente().getConta();
        if (conta.getSaldo().compareTo(compra.getValorVenda()) < 0) {
            throw new SaldoInsuficienteException();
        }

        conta.setSaldo(conta.getSaldo().subtract(compra.getValorVenda()));
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        AtivoEmCarteira item = new AtivoEmCarteira();
        item.setAtivo(compra.getAtivo());
        item.setQuantidadeTotal(compra.getQuantidade());
        item.setValorDeAquisicao(compra.getValorVenda().divide(new BigDecimal(item.getQuantidadeTotal()))); //valor unitario
        item.setValorAtual(item.getValorAtual());
        item.setDesempenho(item.getDesempenho());
        item.setConta(conta);

        itemCarteiraRepository.save(item);
        conta.getCarteira().add(item);
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public List<AtivoEmCarteiraResponseDTO> visualizarCarteira(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        List<AtivoEmCarteira> carteira = cliente.getConta().getCarteira();

        return carteira.stream()
                .map(item -> {
                    Ativo ativo = item.getAtivo();

                    Integer quantidadeTotal = item.getQuantidadeTotal();

                    BigDecimal valorDeAquisicao = item.getValorDeAquisicao();

                    BigDecimal valorAtual = ativo.getCotacao()
                            .multiply(BigDecimal.valueOf(quantidadeTotal));

                    BigDecimal desempenho = valorAtual.subtract(valorDeAquisicao);

                    return AtivoEmCarteiraResponseDTO.builder()
                            .ativoId(ativo.getId())
                            .nomeAtivo(ativo.getNome())
                            .tipo(ativo.getTipo())
                            .quantidadeTotal(quantidadeTotal)
                            .valorDeAquisicao(valorDeAquisicao)
                            .valorAtual(valorAtual)
                            .desempenho(desempenho)
                            .build();
                })
                .collect(Collectors.toList());
    }


}
