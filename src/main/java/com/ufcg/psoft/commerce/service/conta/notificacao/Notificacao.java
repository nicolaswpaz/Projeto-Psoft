package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoGetRequestDTO;

public abstract class Notificacao implements NotificacaoListener {
    @Override
    public void notificarAtivoDisponivel(String nomeCliente, AtivoGetRequestDTO ativoGetRequestDTO){}
}
