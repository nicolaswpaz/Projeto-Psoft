package com.ufcg.psoft.commerce.exception.resgate;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ClienteNaoPossuiEsseAtivoEmCarteiraException extends CommerceException {
    public ClienteNaoPossuiEsseAtivoEmCarteiraException() {
        super("O cliente nao possui esse ativo em carteira!");
    }
}