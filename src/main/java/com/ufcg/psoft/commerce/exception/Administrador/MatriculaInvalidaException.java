package com.ufcg.psoft.commerce.exception.Administrador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class MatriculaInvalidaException extends CommerceException {
    public MatriculaInvalidaException() {
        super("Autenticação falhou!");
    }
}
