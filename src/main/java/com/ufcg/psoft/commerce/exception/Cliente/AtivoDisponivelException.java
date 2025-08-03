package com.ufcg.psoft.commerce.exception.Cliente;

public class AtivoDisponivelException extends RuntimeException {
    public AtivoDisponivelException(){super("O ativo que você marcou interesse está disponível!");}
}
