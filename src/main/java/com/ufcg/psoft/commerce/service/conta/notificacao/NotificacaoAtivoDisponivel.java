package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificacaoAtivoDisponivel implements Notificacao{

    private static final Logger logger = LogManager.getLogger(NotificacaoAtivoDisponivel.class);

    public NotificacaoAtivoDisponivel(){}

    @Override
    public void notificarCliente(String nomeCliente, AtivoResponseDTO ativoResponseDTO){
        logger.info("\nCaro cliente {}, o ativo indisponível que você marcou interesse está disponível!" +
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
