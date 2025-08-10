package com.ufcg.psoft.commerce.exception.ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AtivoNaoExisteException extends CommerceException {
    public AtivoNaoExisteException() {super("O ativo consultado nao existe!");}
}
