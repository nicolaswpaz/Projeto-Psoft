package com.ufcg.psoft.commerce.service.operacao.compra.status;

import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;

public class SolicitadoState implements StatusCompraState{

    private Operacao operacao;

    public SolicitadoState(Operacao operacao) {
        this.operacao = operacao;
    }

    @Override
    public void mover() {
        operacao.setStatusCompra(StatusCompra.DISPONIVEL);
        operacao.setStatusState(new DisponivelState(operacao));
    }

    @Override
    public String getNome() {
        return "SOLICITADO";
    }
}
