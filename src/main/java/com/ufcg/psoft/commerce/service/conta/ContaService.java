package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;

public interface ContaService {

    void adicionarAtivoNaListaDeInteresse(Long idConta, AtivoResponseDTO ativoIndisponivel);

    void notificarClientesComInteresse(Ativo ativo);
}
