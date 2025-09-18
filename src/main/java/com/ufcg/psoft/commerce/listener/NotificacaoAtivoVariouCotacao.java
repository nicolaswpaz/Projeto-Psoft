package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoAtivo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoAtivoVariouCotacao extends NotificacaoAdapter {

    private static final Logger logger = LogManager.getLogger(NotificacaoAtivoVariouCotacao.class);

    @Override
    public void notificarAtivoVariouCotacao(EventoAtivo evento) {
        logger.info("""
                        Caro cliente {}, o ativo que você marcou interesse, teve uma taxa de variação de cotação acima de 10%!
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
