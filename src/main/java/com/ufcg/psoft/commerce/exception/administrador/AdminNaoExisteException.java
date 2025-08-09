package com.ufcg.psoft.commerce.exception.administrador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AdminNaoExisteException extends CommerceException {
    public AdminNaoExisteException() {
            super("Administrador não encontrado.");
    }
}
