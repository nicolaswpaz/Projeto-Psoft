package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.conta.ContaNaoExisteException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.service.conta.notificacao.Notificacao;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoVariouCotacao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Conta criarContaPadrao() {
        Conta conta = Conta.builder()
                .saldo(BigDecimal.valueOf(0.00))
                .ativosDeInteresse(new ArrayList<>())
                .build();

        return contaRepository.save(conta);
    }

    @Override
    public void adicionarAtivoNaListaDeInteresse(Long id, Ativo ativo) {

        Conta conta = contaRepository.findById(id).orElseThrow(ContaNaoExisteException::new);

        if (conta.getAtivosDeInteresse() == null) {
            conta.setAtivosDeInteresse(new ArrayList<>());
        }
        if (!conta.getAtivosDeInteresse().contains(ativo)) {
            conta.getAtivosDeInteresse().add(ativo);
        }

        contaRepository.save(conta);
    }

    private void notificarClientesComInteresse(Ativo ativo, Notificacao notificacao) {
        List<Conta> contas = contaRepository.findAll();

        for (Conta conta : contas) {
            List<Ativo> interesses = conta.getAtivosDeInteresse();

            if (interesses == null || interesses.stream().noneMatch(a -> a.getId().equals(ativo.getId()))) {
                continue;
            }

            clienteRepository.findByContaId(conta.getId()).ifPresent(cliente -> {
                AtivoResponseDTO dto = modelMapper.map(ativo, AtivoResponseDTO.class);
                notificacao.notificarCliente(cliente.getNome(), dto);
            });
        }
    }

    @Override
    public void notificarAtivoDisponivelClientesComInteresse(Ativo ativo) {
        notificarClientesComInteresse(ativo, new NotificacaoAtivoDisponivel());
    }

    @Override
    public void notificarClientesPremiumComInteresse(Ativo ativo){
        notificarClientesComInteresse(ativo, new NotificacaoAtivoVariouCotacao());
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

        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO adicionarNaCarteira(Long idCliente, Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.COMPRADO) {
            throw new StatusCompraInvalidoException();
        }

        compra.avancarStatus();
        compraRepository.save(compra);

        return modelMapper.map(compra, CompraResponseDTO.class);
    }
}
