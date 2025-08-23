package com.ufcg.psoft.commerce.exception.ativocarteira;

public class AtivoCarteiraNaoExisteException extends RuntimeException {
    public AtivoCarteiraNaoExisteException() {super("O Ativo não foi encontrado na carteira/ ou não existe");}
}
