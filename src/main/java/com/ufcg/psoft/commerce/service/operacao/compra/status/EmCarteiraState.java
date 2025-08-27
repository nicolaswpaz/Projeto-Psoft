package com.ufcg.psoft.commerce.service.operacao.compra.status;

import com.ufcg.psoft.commerce.model.Compra;

public class EmCarteiraState implements StatusCompraState {

    final Compra compra;

    public EmCarteiraState(Compra compra) {
        this.compra = compra;
    }

    @Override
    public void mover() {
        //does nothing
    }

    @Override
    public String getNome() {
        return "EM_CARTEIRA";
    }
}
