package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.Operacao;

import java.util.List;

public interface ContaService {

    Conta criarContaPadrao();

    void adicionarAtivoNaListaDeInteresse(Long id, Ativo ativo);

    void notificarAtivoDisponivelClientesComInteresse(Ativo ativo);

    void notificarClientesPremiumComInteresse(Ativo ativo);

    CompraResponseDTO confirmarCompra(Long idCliente, Long idCompra);

    CompraResponseDTO adicionarNaCarteira(Long idCliente, Long idCompra);

    List<AtivoEmCarteiraDTO> visualizarCarteira(Long idCliente, String codigoAcesso);
}
