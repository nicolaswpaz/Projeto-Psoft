package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Conta.ContaResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;

public interface ContaService {

    void adicionarAtivoNaListaDeInteresse(Long idConta, AtivoResponseDTO ativoIndisponivel);

    ContaResponseDTO notificarClientesComInteresse(Ativo ativo);
}
