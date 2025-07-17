package com.ufcg.psoft.commerce.exception.Ativo;

public class AtivoNaoExisteException extends RuntimeException {
    public AtivoNaoExisteException() {super("O ativo consultado não existe!");}
}
