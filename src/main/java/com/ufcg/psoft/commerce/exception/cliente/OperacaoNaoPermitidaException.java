package com.ufcg.psoft.commerce.exception.cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class OperacaoNaoPermitidaException extends CommerceException {
    public OperacaoNaoPermitidaException(String message) {
        super(message);
    }
}