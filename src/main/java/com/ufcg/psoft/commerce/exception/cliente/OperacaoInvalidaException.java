package com.ufcg.psoft.commerce.exception.cliente;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class OperacaoInvalidaException extends CommerceException {
    public OperacaoInvalidaException() {
        super("Não é possivel adicionar um ativo disponivel do tipo Tesouro direto a lista de interesss");
    }
}
