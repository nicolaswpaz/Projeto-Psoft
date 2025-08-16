package com.ufcg.psoft.commerce.service.operacao.compra.status;

import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;

public class CompradoState implements StatusCompraState {
    private Operacao operacao;

    public CompradoState(Operacao operacao) {
        this.operacao = operacao;
    }

    @Override
    public void mover() {
        operacao.setStatusCompra(StatusCompra.EM_CARTEIRA);
        operacao.setStatusState(new EmCarteiraState(operacao));
    }

    @Override
    public String getNome() {
        return "COMPRADO";
    }
}
