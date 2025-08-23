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
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;;
import com.ufcg.psoft.commerce.service.carteira.AtivoEmCarteiraServiceImpl;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoServiceImpl;
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
    AtivoCarteiraRepository ativoCarteiraRepository;

    @Autowired
    NotificacaoServiceImpl notificacaoService;

    @Override
    public Conta criarContaPadrao() {
        Carteira carteira = new Carteira();

        Conta conta = Conta.builder()
                .saldo(BigDecimal.valueOf(0.00))
                .carteira(carteira)
                .build();

        carteira.setConta(conta);

        return contaRepository.save(conta);
    }

//    @Override
//    public void notificarAtivoDisponivelClientesComInteresse(EventoAtivo evento) {
//        notificacaoService.notificarDisponibilidadeAtivo(evento.getAtivo());
//    }
//
//    @Override
//    public void notificarClientesPremiumComInteresse(EventoAtivo evento) {
//        notificacaoService.notificarVariacaoCotacao(evento.getAtivo());
//    }


    @Override
    public CompraResponseDTO confirmarCompra(Long idCliente, Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.DISPONIVEL) {
            throw new StatusCompraInvalidoException();
        }

        Conta conta = compra.getConta();
        if (conta.getSaldo().compareTo(compra.getValorVenda()) < 0) {
            throw new SaldoInsuficienteException();
        }

        conta.setSaldo(conta.getSaldo().subtract(compra.getValorVenda()));
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        AtivoEmCarteira ativoEmCarteira = new AtivoEmCarteira();
        ativoEmCarteira.setAtivo(compra.getAtivo());
        ativoEmCarteira.setQuantidadeTotal(compra.getQuantidade());
        ativoEmCarteira.setValorDeAquisicao(compra.getValorVenda().divide(new BigDecimal(ativoEmCarteira.getQuantidadeTotal())));;
        ativoEmCarteira.setCotacaoAtual(ativoEmCarteira.getCotacaoAtual());
        ativoEmCarteira.setDesempenho(ativoEmCarteira.getDesempenho());

        ativoCarteiraRepository.save(ativoEmCarteira);
        conta.getCarteira().getAtivosEmCarteira().add(ativoEmCarteira);
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public List<AtivoEmCarteiraResponseDTO> visualizarCarteira(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        List<AtivoEmCarteira> carteira = cliente.getConta().getCarteira().getAtivosEmCarteira();

        return carteira.stream()
                .map(ativoEmCarteira -> {
                    Ativo ativo = ativoEmCarteira.getAtivo();

                    Integer quantidadeTotal = ativoEmCarteira.getQuantidadeTotal();

                    BigDecimal valorDeAquisicao = ativoEmCarteira.getValorDeAquisicao();

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

    @Override
    public void acrecentaSaldoConta(Long idCliente, BigDecimal valor) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        Conta conta = cliente.getConta();
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);
    }


}
