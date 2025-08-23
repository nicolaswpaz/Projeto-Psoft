package com.ufcg.psoft.commerce.service.notificacao;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Compra;

public interface NotificacaoService {
    void notificarDisponibilidadeAtivo(Ativo ativo);

    void notificarVariacaoCotacao(Ativo ativo);

    void notificarDisponibilidadeCompra(Compra compra);
}
