package com.ufcg.psoft.commerce.exception.cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CodigoDeAcessoInvalidoException extends CommerceException {
    public CodigoDeAcessoInvalidoException() {
        super("Codigo de acesso invalido!");
    }
}
