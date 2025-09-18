package com.ufcg.psoft.commerce.exception.conta;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaldoInsuficienteException extends CommerceException {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente!");
    }
}
