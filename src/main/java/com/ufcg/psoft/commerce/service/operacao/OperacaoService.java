package com.ufcg.psoft.commerce.service.operacao;

import com.ufcg.psoft.commerce.dto.Operacao.OperacaoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Operacao.OperacaoResponseDTO;

import java.util.List;

public interface OperacaoService {
    OperacaoResponseDTO criarOperacaoCompra(OperacaoPostPutRequestDTO compraPostPutRequestDTO);

    OperacaoResponseDTO buscarOperacaoCompra(Long id);

    List<OperacaoResponseDTO> listarOperacoesCompras();
}
