package com.ufcg.psoft.commerce.exception.Cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class OperacaoNaoPermitidaException extends CommerceException {
    public OperacaoNaoPermitidaException(String message) {
        super(message);
    }
}