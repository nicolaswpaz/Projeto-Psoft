package com.ufcg.psoft.commerce.exception.administrador;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AdminJaExisteException extends CommerceException {
    public AdminJaExisteException() {
            super("Ja existe um administrador cadastrado no sistema.");
    }
}
