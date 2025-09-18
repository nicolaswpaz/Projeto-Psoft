package com.ufcg.psoft.commerce.service.operacao.resgate.status;

import com.ufcg.psoft.commerce.model.Resgate;

public class EmContaState implements StatusResgateState{
    private Resgate resgate;

    public EmContaState(Resgate resgate) {
        this.resgate = resgate;
    }

    @Override
    public void mover() {
        // does nothing
    }

    @Override
    public String getNome() {
        return "EM_CONTA";
    }
}
