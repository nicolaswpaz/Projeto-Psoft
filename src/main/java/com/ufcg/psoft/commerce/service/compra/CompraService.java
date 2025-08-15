package com.ufcg.psoft.commerce.service.compra;

import com.ufcg.psoft.commerce.dto.Operacao.OperacaoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Operacao.OperacaoResponseDTO;

import java.util.List;

public interface CompraService {
    OperacaoResponseDTO criarCompra(OperacaoPostPutRequestDTO compraPostPutRequestDTO);

    List<OperacaoResponseDTO> listarCompras();

    OperacaoResponseDTO buscarCompra(Long id);
}
