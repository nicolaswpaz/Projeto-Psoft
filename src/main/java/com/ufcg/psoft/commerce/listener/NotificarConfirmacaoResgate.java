package com.ufcg.psoft.commerce.listener;

import com.ufcg.psoft.commerce.events.EventoResgate;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Resgate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class NotificarConfirmacaoResgate extends NotificacaoAdapter{

    private static final Logger logger = LogManager.getLogger(NotificarConfirmacaoResgate.class);

    @Override
    public void notificarConfirmacaoResgate(EventoResgate evento) {
        Resgate resgate = evento.getResgate();
        Cliente cliente = evento.getCliente();

        logger.info("""
                        Caro cliente {}, o resgate que você solicitou, esta confirmado!"
                        Dados do resgate:
                        Nome do ativo resgatado: {}
                        O tipo do ativo resgatado: {}
                        Quantidade de ativos resgatados: {}
                        Data de solicitação: {}
                        """,

                cliente.getNome(),
                resgate.getAtivo().getNome(),
                resgate.getAtivo().getTipo(),
                resgate.getQuantidade(),
                resgate.getDataSolicitacao()
        );
    }
}
