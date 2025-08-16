package com.ufcg.psoft.commerce.service.operacao.compra.status;

import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;

public class DisponivelState implements StatusCompraState {

    private Operacao operacao;

    public DisponivelState(Operacao operacao) {
        this.operacao = operacao;
    }

    @Override
    public void mover() {
        operacao.setStatusCompra(StatusCompra.COMPRADO);
        operacao.setStatusState(new CompradoState(operacao));
    }

    @Override
    public String getNome() {
        return "DISPONIVEL";
    }
}
