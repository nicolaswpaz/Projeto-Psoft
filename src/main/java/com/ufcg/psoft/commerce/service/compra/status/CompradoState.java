package com.ufcg.psoft.commerce.service.compra.status;

import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;

public class CompradoState implements StatusCompraState {
    private Compra compra;

    public CompradoState(Compra compra) {
        this.compra = compra;
    }

    @Override
    public void mover() {
        compra.setStatusCompra(StatusCompra.EM_CARTEIRA);
        compra.setStatusState(new EmCarteiraState(compra));
    }

    @Override
    public String getNome() {
        return "COMPRADO";
    }
}
