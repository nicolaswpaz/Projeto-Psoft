package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.model.Conta;

import java.util.List;

public interface ContaService {

    Conta criarContaPadrao();

    void notificarAtivoDisponivelClientesComInteresse(EventoAtivo evento);

    void notificarClientesPremiumComInteresse(EventoAtivo evento);

    CompraResponseDTO confirmarCompra(Long idCliente, Long idCompra);

    List<AtivoEmCarteiraResponseDTO> visualizarCarteira(Long idCliente);
}
