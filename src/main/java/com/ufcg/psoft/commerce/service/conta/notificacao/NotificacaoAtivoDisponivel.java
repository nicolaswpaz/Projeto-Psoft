package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoGetRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotificacaoAtivoDisponivel extends Notificacao{

    private static final Logger logger = LogManager.getLogger(NotificacaoAtivoDisponivel.class);

    public NotificacaoAtivoDisponivel(){}

    @Override
    public void notificarAtivoDisponivel(String nomeCliente, AtivoGetRequestDTO ativoGetRequestDTO){
        logger.info("\nCaro cliente {}, o ativo indisponível que você marcou interesse está em disponível!" +
                        "\nDados do Ativo:" +
                        "\nNome do ativo: {}" +
                        "\nO tipo do ativo: {}" +
                        "\nCotação do ativo: {}" +
                        "\nDescrição do ativo: {}",
                nomeCliente,
                ativoGetRequestDTO.getAtivo().getNome(),
                ativoGetRequestDTO.getAtivo().getTipo(),
                ativoGetRequestDTO.getAtivo().getCotacao(),
                ativoGetRequestDTO.getAtivo().getDescricao()
        );
    }
}
