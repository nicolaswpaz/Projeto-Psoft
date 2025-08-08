package com.ufcg.psoft.commerce.exception.conta;

public class OperacaoInvalidaException extends RuntimeException {
    public OperacaoInvalidaException() {
        super("Não é possivel adicionar um ativo disponivel do tipo Tesouro direto a lista de interesss");
    }
}
