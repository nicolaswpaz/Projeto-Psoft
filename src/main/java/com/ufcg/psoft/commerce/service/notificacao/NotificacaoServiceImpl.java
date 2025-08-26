package com.ufcg.psoft.commerce.service.notificacao;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.events.EventoCompra;
import com.ufcg.psoft.commerce.events.EventoResgate;
import com.ufcg.psoft.commerce.listener.NotificacaoListener;
import com.ufcg.psoft.commerce.listener.NotificarConfirmacaoResgate;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import com.ufcg.psoft.commerce.repository.InteresseAtivoRepository;
import com.ufcg.psoft.commerce.repository.InteresseCompraRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificacaoServiceImpl implements NotificacaoService{

    private final InteresseAtivoRepository interesseAtivoRepository;
    private final InteresseCompraRepository interesseCompraRepository;
    private final List<NotificacaoListener> listeners;

    public NotificacaoServiceImpl(InteresseAtivoRepository interesseAtivoRepository,
                                  InteresseCompraRepository interesseCompraRepository,
                                  List<NotificacaoListener> listeners) {
        this.interesseAtivoRepository = interesseAtivoRepository;
        this.interesseCompraRepository = interesseCompraRepository;
        this.listeners = listeners;
    }

    @Override
    public void notificarDisponibilidadeAtivo(Ativo ativo) {
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

    @Override
    public void notificarDisponibilidadeCompra(Compra compra) {
        List<InteresseCompra> interesses = interesseCompraRepository
                .findByCompra(compra);

        interesses.forEach(interesse -> {
            EventoCompra evento = new EventoCompra(compra, interesse.getCliente());
            listeners.forEach(listener -> listener.notificarCompraDisponivel(evento));
        });
    }

    @Override
    public void notificarConfirmacacaoResgate(Resgate resgate) {
        EventoResgate evento = new EventoResgate(resgate, resgate.getCliente());
        listeners.stream()
                .filter(l -> l instanceof NotificarConfirmacaoResgate)
                .findFirst()
                .ifPresent(l -> l.notificarConfirmacaoResgate(evento));
    }
}
