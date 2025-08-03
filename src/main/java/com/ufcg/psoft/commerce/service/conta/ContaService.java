package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Conta.ContaResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoListener;

public interface ContaService {

    void adicionarAtivoNaListaDeInteresse(Long idConta, AtivoResponseDTO ativoIndisponivel);

    ContaResponseDTO notificarAtivoDisponivelClientesComInteresse(Ativo ativo);

    ContaResponseDTO notificarClientesPremiumComInteresse(Ativo ativo);

    Conta salvar(Conta conta);
}
