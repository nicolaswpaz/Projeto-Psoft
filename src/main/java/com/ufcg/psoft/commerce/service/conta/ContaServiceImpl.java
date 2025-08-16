package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.exception.conta.ContaNaoExisteException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.service.conta.notificacao.Notificacao;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoVariouCotacao;
import com.ufcg.psoft.commerce.service.operacao.OperacaoService;
import com.ufcg.psoft.commerce.service.operacao.OperacaoServiceImpl;
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
    OperacaoService operacaoService;

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
    public void efetuarCompraAtivo(Cliente cliente, Ativo ativo, int quantidade) {

        Conta conta = contaRepository.findById(cliente.getId()).orElseThrow(ContaNaoExisteException::new);

        if (conta.getSaldo().compareTo(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao())) < 0) {
            throw new SaldoInsuficienteException();
        }

        conta.getOperacoes().add(operacaoService.criarOperacaoCompra(cliente, ativo, quantidade));
    }

}
