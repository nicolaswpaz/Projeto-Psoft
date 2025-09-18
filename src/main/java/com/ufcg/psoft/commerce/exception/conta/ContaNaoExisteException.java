package com.ufcg.psoft.commerce.exception.conta;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ContaNaoExisteException extends CommerceException {
    public ContaNaoExisteException() {
        super("Conta nao encontrada");
    }
}
