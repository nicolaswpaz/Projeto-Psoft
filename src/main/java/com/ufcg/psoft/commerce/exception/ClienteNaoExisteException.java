package com.ufcg.psoft.commerce.exception;

public class ClienteNaoExisteException extends CommerceException {
    public ClienteNaoExisteException() {
        super("O cliente consultado nao existe!");
    }
}
