package com.ufcg.psoft.commerce.exception.resgate;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ResgateNaoExisteException extends CommerceException {
    public ResgateNaoExisteException() {
        super("O resgate nao existe!");
    }
}
