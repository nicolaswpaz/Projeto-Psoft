package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;

public interface NotificacaoListener {

    void notificarAtivoDisponivel(EventoAtivo evento);
    void notificarAtivoVariouCotacao(EventoAtivo evento);
}
