package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoGetRequestDTO;

public interface NotificacaoListener {
    void notificarAtivoDisponivel(String nomeCliente, AtivoGetRequestDTO ativoGetRequestDTO);
}
