package com.ufcg.psoft.commerce.exception.cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ClienteNaoExisteException extends CommerceException {
    public ClienteNaoExisteException() {
        super("O cliente consultado nao existe!");
    }
}
