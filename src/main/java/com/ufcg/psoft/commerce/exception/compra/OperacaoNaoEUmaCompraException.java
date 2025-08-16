package com.ufcg.psoft.commerce.exception.compra;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class OperacaoNaoEUmaCompraException extends CommerceException {
    public OperacaoNaoEUmaCompraException() {
        super("Operacao nao e uma compra!");
    }
}
