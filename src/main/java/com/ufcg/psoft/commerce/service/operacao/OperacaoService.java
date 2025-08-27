package com.ufcg.psoft.commerce.service.operacao;

import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface OperacaoService {


    List<OperacaoResponseDTO> consultarOperacaoComCLiente(Long idCliente, String codigoAcesso, String tipoAtivo, LocalDate dataInicio, LocalDate dataFim, String statusOperacao);

    List<OperacaoResponseDTO> consultarOperacoesComAdmin(String matriculaAdmin, Long idCliente, String tipoAtivo, LocalDate data, String tipoOperacao);
}
