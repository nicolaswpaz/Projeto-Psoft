package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.events.EventoCompra;
import com.ufcg.psoft.commerce.events.EventoResgate;

public abstract class NotificacaoAdapter implements NotificacaoListener{
    @Override
    public void notificarAtivoDisponivel(EventoAtivo evento) {
        // faz nada
    }

    @Override
    public void notificarAtivoVariouCotacao(EventoAtivo evento) {
        // faz nada
    }

    @Override
    public void notificarCompraDisponivel(EventoCompra evento) {
        // faz nada
    }

    @Override
    public void notificarConfirmacaoResgate(EventoResgate evento) {
        // faz nada
    }
}
