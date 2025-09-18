package com.ufcg.psoft.commerce.exception.resgate;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ResgateNaoPertenceAoClienteException extends CommerceException {
    public ResgateNaoPertenceAoClienteException() {
        super("O resgate nao pertence a esse cliente!");
    }
}
