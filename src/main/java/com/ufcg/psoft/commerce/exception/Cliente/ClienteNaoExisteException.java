package com.ufcg.psoft.commerce.exception.Cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ClienteNaoExisteException extends CommerceException {
    public ClienteNaoExisteException() {
        super("O cliente consultado nao existe!");
    }
}
