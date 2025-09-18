package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoAtivoDisponivel extends NotificacaoAdapter {

    private static final Logger logger = LogManager.getLogger(NotificacaoAtivoDisponivel.class);

    @Override
    public void notificarAtivoDisponivel(EventoAtivo evento) {
        logger.info("""
                        Caro cliente {}, o ativo indisponível que você marcou interesse está disponível!
                        Dados do Ativo:
                        Nome do ativo: {}
                        O tipo do ativo: {}
                        Cotação do ativo: {}
                        Descrição do ativo: {}
                        """,
                evento.getCliente().getNome(),
                evento.getAtivo().getNome(),
                evento.getAtivo().getTipo(),
                evento.getAtivo().getCotacao(),
                evento.getAtivo().getDescricao()
        );
    }
}
