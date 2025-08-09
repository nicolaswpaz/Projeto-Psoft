package com.ufcg.psoft.commerce.service.conta.notificacao;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;

public interface Notificacao {

    void notificarCliente(String nomeCliente, AtivoResponseDTO ativoResponseDTO);
}
