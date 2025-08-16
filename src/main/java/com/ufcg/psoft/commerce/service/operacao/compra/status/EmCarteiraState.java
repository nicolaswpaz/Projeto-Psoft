package com.ufcg.psoft.commerce.service.operacao.compra.status;

import com.ufcg.psoft.commerce.model.Operacao;

public class EmCarteiraState implements StatusCompraState {

    private Operacao operacao;

    public EmCarteiraState(Operacao operacao) {
        this.operacao = operacao;
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
