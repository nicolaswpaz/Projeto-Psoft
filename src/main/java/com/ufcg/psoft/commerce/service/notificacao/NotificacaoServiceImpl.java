package com.ufcg.psoft.commerce.service.notificacao;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import com.ufcg.psoft.commerce.listener.NotificacaoListener;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.InteresseAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import com.ufcg.psoft.commerce.repository.InteresseAtivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificacaoServiceImpl implements NotificacaoService{

    @Autowired
    InteresseAtivoRepository interesseAtivoRepository;

    @Autowired(required = false)
    List<NotificacaoListener> listeners = new ArrayList<>();

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
