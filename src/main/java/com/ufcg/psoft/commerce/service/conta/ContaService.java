package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.model.Conta;

import java.math.BigDecimal;
import java.util.List;

public interface ContaService {

    Conta criarContaPadrao();

    CompraResponseDTO confirmarCompra(Long idCliente, Long idCompra);

    List<AtivoEmCarteiraResponseDTO> visualizarCarteira(Long idCliente);

    void acrecentaSaldoConta(Long idCliente, BigDecimal valor);
}
