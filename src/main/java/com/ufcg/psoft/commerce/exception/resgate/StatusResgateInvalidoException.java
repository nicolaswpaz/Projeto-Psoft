package com.ufcg.psoft.commerce.exception.resgate;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class StatusResgateInvalidoException extends CommerceException {
    public StatusResgateInvalidoException() {
        super("Status de resgate nao permite essa acao");
    }
}
