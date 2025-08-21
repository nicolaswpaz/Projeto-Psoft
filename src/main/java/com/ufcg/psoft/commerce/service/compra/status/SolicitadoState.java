package com.ufcg.psoft.commerce.service.compra.status;

import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;

public class SolicitadoState implements StatusCompraState{

    private Compra compra;

    public SolicitadoState(Compra compra) {
        this.compra = compra;
    }

    @Override
    public void mover() {
        compra.setStatusCompra(StatusCompra.DISPONIVEL);
        compra.setStatusState(new DisponivelState(compra));
    }

    @Override
    public String getNome() {
        return "SOLICITADO";
    }
}
