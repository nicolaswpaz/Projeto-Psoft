package com.ufcg.psoft.commerce.listener;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.events.EventoResgate;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Resgate;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
import com.ufcg.psoft.commerce.service.resgate.status.StatusResgateState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class NotificarConfirmacaoResgate extends NotificacaoAdapter{

    private static final Logger logger = LogManager.getLogger(NotificarConfirmacaoResgate.class);

    @Override
    public void notificarConfirmacaoResgate(EventoResgate evento) {
        Resgate resgate = evento.getResgate();
        Cliente cliente = evento.getCliente();

        logger.info("""
                        Caro cliente {}, o resgate que você solicitou, esta confirmado e em conta!"
                        Dados do resgate:
                        Nome do ativo resgatado: {}
                        O tipo do ativo resgatado: {}
                        Quantidade de ativos resgatados: {}
                        Valor resgatado: {}
                        Lucro: {}
                        Imposto: {}
                        Saldo em conta: {}
                        Data de solicitação: {}
                        """,

                cliente.getNome(),
                resgate.getAtivo().getNome(),
                resgate.getAtivo().getTipo(),
                resgate.getQuantidade(),
                resgate.getValorResgatado(),
                resgate.getLucro(),
                resgate.getImposto(),
                cliente.getConta().getSaldo(),
                resgate.getDataSolicitacao()
        );
    }
}
