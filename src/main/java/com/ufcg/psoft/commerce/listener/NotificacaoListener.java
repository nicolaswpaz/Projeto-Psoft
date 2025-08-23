package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.events.EventoCompra;

public interface NotificacaoListener {

    void notificarAtivoDisponivel(EventoAtivo evento);
    void notificarAtivoVariouCotacao(EventoAtivo evento);
    void notificarCompraDisponivel(EventoCompra evento);
}
