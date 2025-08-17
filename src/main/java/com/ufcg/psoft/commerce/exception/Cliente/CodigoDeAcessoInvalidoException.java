package com.ufcg.psoft.commerce.exception.Cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CodigoDeAcessoInvalidoException extends CommerceException {
    public CodigoDeAcessoInvalidoException() {
        super("Codigo de acesso invalido!");
    }
}
