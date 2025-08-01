package com.ufcg.psoft.commerce.exception.Conta;

public class ContaNaoExisteException extends RuntimeException {
    public ContaNaoExisteException() {
        super("Conta não encontrada");
    }
}
