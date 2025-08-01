package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;

public interface NotificacaoListener {
    void notificarAtivoDisponivel(String nomeCliente, AtivoResponseDTO ativoResponseDTO);
}
