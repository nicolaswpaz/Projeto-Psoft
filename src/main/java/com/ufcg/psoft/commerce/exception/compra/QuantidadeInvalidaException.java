package com.ufcg.psoft.commerce.exception.compra;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class QuantidadeInvalidaException extends CommerceException {
    public QuantidadeInvalidaException() {
        super("Quantidade invalida!");
    }
}
