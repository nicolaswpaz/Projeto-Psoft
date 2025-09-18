package com.ufcg.psoft.commerce.exception.compra;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CompraNaoPertenceAoClienteException extends CommerceException {
    public CompraNaoPertenceAoClienteException() {
        super("A compra nao pertence a esse cliente!");
    }
}
