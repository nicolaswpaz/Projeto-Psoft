package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.events.EventoCompra;
import com.ufcg.psoft.commerce.events.EventoResgate;
import com.ufcg.psoft.commerce.model.Resgate;

public interface NotificacaoListener {

    void notificarAtivoDisponivel(EventoAtivo evento);
    void notificarAtivoVariouCotacao(EventoAtivo evento);
    void notificarCompraDisponivel(EventoCompra evento);
    void notificarConfirmacaoResgate(EventoResgate evento);
}
