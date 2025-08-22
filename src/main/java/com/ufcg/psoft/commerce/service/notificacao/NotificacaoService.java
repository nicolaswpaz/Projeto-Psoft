package com.ufcg.psoft.commerce.service.notificacao;

import com.ufcg.psoft.commerce.model.Ativo;

public interface NotificacaoService {
    void notificarDisponibilidade(Ativo ativo);

    void notificarVariacaoCotacao(Ativo ativo);
}
