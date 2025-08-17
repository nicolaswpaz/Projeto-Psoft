package com.ufcg.psoft.commerce.exception.Administrador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AdminNaoExisteException extends CommerceException {
    public AdminNaoExisteException() {
            super("Administrador não encontrado.");
    }
}
