package com.ufcg.psoft.commerce.exception.ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AtivoDisponivelException extends CommerceException {
    public AtivoDisponivelException(){super("O ativo está disponível!");}
}
