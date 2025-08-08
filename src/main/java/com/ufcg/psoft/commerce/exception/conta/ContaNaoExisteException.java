package com.ufcg.psoft.commerce.exception.conta;

public class ContaNaoExisteException extends RuntimeException {
    public ContaNaoExisteException() {
        super("Conta não encontrada");
    }
}
