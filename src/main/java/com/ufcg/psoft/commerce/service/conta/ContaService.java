package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Conta;

public interface ContaService {

    Conta criarContaPadrao();

    void adicionarAtivoNaListaDeInteresse(Long idConta, AtivoResponseDTO ativo);

    void notificarAtivoDisponivelClientesComInteresse(Ativo ativo);

    void notificarClientesPremiumComInteresse(Ativo ativo);

}
