package com.ufcg.psoft.commerce.exception.Cliente;

public class AtivoIndisponivelException extends RuntimeException {
    public AtivoIndisponivelException(){super("O ativo que você marcou interesse está indisponível!");}
}
