package com.ufcg.psoft.commerce.service.operacao.compra.status;

import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;

public class DisponivelState implements StatusCompraState {

    private Compra compra;

    public DisponivelState(Compra compra) {
        this.compra = compra;
    }

    @Override
    public void mover() {
        compra.setStatusCompra(StatusCompra.COMPRADO);
        compra.setStatusState(new CompradoState(compra));
    }

    @Override
    public String getNome() {
        return "DISPONIVEL";
    }
}
