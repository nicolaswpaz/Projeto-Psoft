package com.ufcg.psoft.commerce.service.operacao.resgate.status;

import com.ufcg.psoft.commerce.model.Resgate;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;

public class ConfirmadoState implements StatusResgateState{
    private Resgate resgate;

    public ConfirmadoState(Resgate resgate) {
        this.resgate = resgate;
    }

    @Override
    public void mover() {
        resgate.setStatusResgate(StatusResgate.EM_CONTA);
        resgate.setStatusState(new EmContaState(resgate));
    }

    @Override
    public String getNome() {
        return "CONFIRMADO";
    }
}
