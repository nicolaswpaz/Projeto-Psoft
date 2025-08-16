package com.ufcg.psoft.commerce.exception.operacao;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class OperacaoNaoExisteException extends CommerceException {
    public OperacaoNaoExisteException() {
        super("Operacao nao existe");
    }
}
