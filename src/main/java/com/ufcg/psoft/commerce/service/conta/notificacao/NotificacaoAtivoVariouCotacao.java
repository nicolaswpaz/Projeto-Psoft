package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificacaoAtivoVariouCotacao implements Notificacao{

    private static final Logger logger = LogManager.getLogger(NotificacaoAtivoVariouCotacao.class);

    public NotificacaoAtivoVariouCotacao(){}

    @Override
    public void notificarCliente(String nomeCliente, AtivoResponseDTO ativoResponseDTO){
        logger.info("\nCaro cliente {}, o ativo que você marcou interesse, teve uma taxa de variação de cotação acima de 10%!" +
                        "\nDados do Ativo:" +
                        "\nNome do ativo: {}" +
                        "\nO tipo do ativo: {}" +
                        "\nCotação do ativo: {}" +
                        "\nDescrição do ativo: {}",
                nomeCliente,
                ativoResponseDTO.getNome(),
                ativoResponseDTO.getTipo(),
                ativoResponseDTO.getCotacao(),
                ativoResponseDTO.getDescricao()
        );
    }
}
