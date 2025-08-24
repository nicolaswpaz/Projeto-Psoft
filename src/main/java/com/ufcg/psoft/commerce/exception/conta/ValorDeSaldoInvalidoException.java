package com.ufcg.psoft.commerce.exception.conta;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ValorDeSaldoInvalidoException extends CommerceException {
    public ValorDeSaldoInvalidoException() {
        super("Valores negativos nao podem ser adicionados a carteira!");
    }
}
