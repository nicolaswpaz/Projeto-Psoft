package com.ufcg.psoft.commerce.service.notificacao;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.Resgate;

public interface NotificacaoService {
    void notificarDisponibilidadeAtivo(Ativo ativo);

    void notificarVariacaoCotacao(Ativo ativo);

    void notificarDisponibilidadeCompra(Compra compra);

    void notificarConfirmacacaoResgate(Resgate resgate);
}
