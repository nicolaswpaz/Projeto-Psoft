package com.ufcg.psoft.commerce.exception.compra;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CompraNaoExisteException extends CommerceException {
    public CompraNaoExisteException() {
        super("A compra não existe");
    }
}