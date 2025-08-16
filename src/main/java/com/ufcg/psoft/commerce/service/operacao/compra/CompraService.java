package com.ufcg.psoft.commerce.service.operacao.compra;

import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;

import java.util.List;

public interface CompraService {

    CompraResponseDTO solicitarCompra(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade);

    CompraResponseDTO disponibilizarCompra(Long idCompra, String matriculaAdmin);

    CompraResponseDTO confirmarCompra(Long idCliente, String codigoAcesso, Long idCompra);

    CompraResponseDTO adicionarNaCarteira(Long idCliente, String codigoAcesso, Long idCompra);

    CompraResponseDTO consultar(Long idCliente, String codigoAcesso, Long idCompra);

    List<CompraResponseDTO> listar(String matriculaAdmin);
}
