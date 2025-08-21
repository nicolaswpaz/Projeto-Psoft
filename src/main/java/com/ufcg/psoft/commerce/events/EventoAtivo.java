package com.ufcg.psoft.commerce.events;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;

public class EventoAtivo {

    private Ativo ativo;
    private Cliente cliente;

    public EventoAtivo(Ativo ativo, Cliente cliente) {
        this.ativo = ativo;
        this.cliente = cliente;
    }

    public Ativo getAtivo() {
        return this.ativo;
    }

    public Cliente getCliente() {
        return this.cliente;
    }
}
