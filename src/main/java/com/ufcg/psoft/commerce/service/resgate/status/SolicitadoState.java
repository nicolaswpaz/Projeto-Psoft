package com.ufcg.psoft.commerce.service.resgate.status;

import com.ufcg.psoft.commerce.model.Resgate;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;

public class SolicitadoState implements StatusResgateState{
    private Resgate resgate;

    public SolicitadoState(Resgate resgate) {
        this.resgate = resgate;
    }

    @Override
    public void mover() {
        resgate.setStatusResgate(StatusResgate.CONFIRMADO);
        resgate.setStatusState(new ConfirmadoState(resgate));
    }

    @Override
    public String getNome() {
        return "SOLICITADO";
    }
}
