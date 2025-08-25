package com.ufcg.psoft.commerce.exception.resgate;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class SaldoInsuficienteException extends CommerceException {
    public SaldoInsuficienteException(int quantidadeSolicitada, int quantidadeDisponivel) {
        super("Saldo insuficiente: cliente tentou resgatar " + quantidadeSolicitada +
                " unidades desse ativo, mas possui apenas " + quantidadeDisponivel + " na carteira.");
    }
}
