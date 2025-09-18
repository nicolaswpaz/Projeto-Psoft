package com.ufcg.psoft.commerce.service.operacao;

import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import com.ufcg.psoft.commerce.service.autenticacao.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperacaoServiceImpl implements OperacaoService {

    private final AutenticacaoService autenticacaoService;
    private final OperacaoRepository operacaoRepository;

    @Override
    public List<OperacaoResponseDTO> consultarOperacaoCliente(Long idCliente, String codigoAcesso, String tipoAtivo, LocalDate dataInicio, LocalDate dataFim, String statusOperacao) {
        autenticacaoService.autenticarCliente(idCliente, codigoAcesso);

        List<Operacao> operacoesCliente = operacaoRepository.findByClienteId(idCliente);

        return operacoesCliente.stream()
                .filter(op -> tipoAtivo == null || op.getAtivo().getTipo().name().equalsIgnoreCase(tipoAtivo))
                .filter(op -> dataInicio == null || !op.getDataSolicitacao().isBefore(dataInicio))
                .filter(op -> dataFim == null || !op.getDataSolicitacao().isAfter(dataFim))
                .filter(op -> statusOperacao == null || op.getStatusAtual().equalsIgnoreCase(statusOperacao))
                .map(OperacaoResponseDTO::new)
                .toList();
    }

    @Override
    public List<OperacaoResponseDTO> consultarOperacoesComAdmin(String matriculaAdmin, Long idCliente, String tipoAtivo, LocalDate data, String tipoOperacao) {
        autenticacaoService.autenticarAdmin(matriculaAdmin);

        List<Operacao> todasOperacoes = operacaoRepository.findAll();

        return todasOperacoes.stream()
                .filter(op -> idCliente == null || op.getCliente().getId().equals(idCliente))
                .filter(op -> tipoAtivo == null || op.getAtivo().getTipo().name().equalsIgnoreCase(tipoAtivo))
                .filter(op -> data == null || op.getDataSolicitacao().isEqual(data))
                .filter(op -> tipoOperacao == null || op.getClass().getSimpleName().equalsIgnoreCase(tipoOperacao))
                .map(OperacaoResponseDTO::new)
                .toList();
    }
}
