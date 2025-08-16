package com.ufcg.psoft.commerce.exception.compra;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class StatusCompraInvalidoException extends CommerceException {
    public StatusCompraInvalidoException() {
        super("Status de compra nao permite essa acao");
    }
}
