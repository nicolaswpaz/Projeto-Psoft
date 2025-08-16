package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Conta;

public interface ContaService {

    Conta criarContaPadrao();

    void adicionarAtivoNaListaDeInteresse(Long id, Ativo ativo);

    void notificarAtivoDisponivelClientesComInteresse(Ativo ativo);

    void notificarClientesPremiumComInteresse(Ativo ativo);


    void efetuarCompraAtivo(Cliente cliente, Ativo ativo, int quantidade);
}
