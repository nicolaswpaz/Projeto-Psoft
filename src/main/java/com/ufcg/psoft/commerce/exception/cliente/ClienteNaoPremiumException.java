package com.ufcg.psoft.commerce.exception.cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ClienteNaoPremiumException extends CommerceException {
    public ClienteNaoPremiumException() {
        super("Esta funcionalidade está disponível apenas para clientes Premium.");
    }
}