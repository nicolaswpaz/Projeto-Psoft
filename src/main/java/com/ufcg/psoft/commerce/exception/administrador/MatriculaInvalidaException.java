package com.ufcg.psoft.commerce.exception.administrador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class MatriculaInvalidaException extends CommerceException {
    public MatriculaInvalidaException() {
        super("Autenticacao falhou!");
    }
}
