package com.ufcg.psoft.commerce.listener;


import com.ufcg.psoft.commerce.events.EventoCompra;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Compra;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoCompraDisponivel extends NotificacaoAdapter {

    private static final Logger logger = LogManager.getLogger(NotificacaoCompraDisponivel.class);

    @Override
    public void notificarCompraDisponivel(EventoCompra evento) {
        Compra compra = evento.getCompra();
        Cliente cliente = evento.getCliente();

        logger.info("""
                        Caro cliente {}, a compra que você solicitou, está disponível!" +
                        Dados da compra:
                        Nome do ativo comprado: {}
                        O tipo do ativo comprado: {}
                        Valor do ativo no momento da compra: {}
                        Valor total da compra: {}
                        Quantidade de ativos comprados {}
                        Data de solicitação: {}
                        """,

                cliente.getNome(),
                compra.getAtivo().getNome(),
                compra.getAtivo().getTipo(),
                compra.getValorVenda(),
                compra.getValorTotal(),
                compra.getQuantidade(),
                compra.getDataSolicitacao()
        );
    }
}
