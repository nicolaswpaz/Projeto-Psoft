package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;

public abstract class Notificacao implements NotificacaoListener {
    @Override
    public void notificarCliente(String nomeCliente, AtivoResponseDTO ativoResponseDTO){}
}
