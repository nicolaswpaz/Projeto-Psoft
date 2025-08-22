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
        logger.info("\nCaro cliente {}, o ativo que você marcou interesse, teve uma taxa de variação de cotação acima de 10%!" +
                        "\nDados do Ativo:" +
                        "\nNome do ativo: {}" +
                        "\nO tipo do ativo: {}" +
                        "\nCotação do ativo: {}" +
                        "\nDescrição do ativo: {}",
                evento.getCliente().getNome(),
                evento.getAtivo().getNome(),
                evento.getAtivo().getTipo(),
                evento.getAtivo().getCotacao(),
                evento.getAtivo().getDescricao()
        );
    }
}
