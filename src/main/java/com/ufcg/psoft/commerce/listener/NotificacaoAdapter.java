package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;

public class NotificacaoAdapter implements NotificacaoListener{
    @Override
    public void notificarAtivoDisponivel(EventoAtivo evento) {
        // faz nada
    }

    @Override
    public void notificarAtivoVariouCotacao(EventoAtivo evento) {
        // faz nada
    }
}
