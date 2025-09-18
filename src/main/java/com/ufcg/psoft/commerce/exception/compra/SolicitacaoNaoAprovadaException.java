package com.ufcg.psoft.commerce.exception.compra;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SolicitacaoNaoAprovadaException extends CommerceException {
    public SolicitacaoNaoAprovadaException() {
        super("Solicitacao de compra nao aprovada pelo administrador!");
    }
}
