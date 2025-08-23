package com.ufcg.psoft.commerce.service.notificacao;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.listener.NotificacaoListener;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.InteresseAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import com.ufcg.psoft.commerce.repository.InteresseAtivoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificacaoServiceImpl implements NotificacaoService{

    private final InteresseAtivoRepository interesseAtivoRepository;
    private final List<NotificacaoListener> listeners;

    public NotificacaoServiceImpl(InteresseAtivoRepository interesseAtivoRepository,
                                  List<NotificacaoListener> listeners) {
        this.interesseAtivoRepository = interesseAtivoRepository;
        this.listeners = listeners;
    }

    @Override
    public void notificarDisponibilidade(Ativo ativo) {
        List<InteresseAtivo> interesses = interesseAtivoRepository
                .findByAtivoAndTipoInteresse(ativo, TipoInteresse.DISPONIBILIDADE);

        interesses.forEach(interesse -> {
            EventoAtivo evento = new EventoAtivo(ativo, interesse.getCliente());
            listeners.forEach(listener -> listener.notificarAtivoDisponivel(evento));
        });
    }

    @Override
    public void notificarVariacaoCotacao(Ativo ativo) {
        List<InteresseAtivo> interesses = interesseAtivoRepository
                .findByAtivoAndTipoInteresse(ativo, TipoInteresse.VARIACAO_COTACAO);

        interesses.forEach(interesse -> {
            EventoAtivo evento = new EventoAtivo(ativo, interesse.getCliente());
            listeners.forEach(listener -> listener.notificarAtivoVariouCotacao(evento));
        });
    }
}
