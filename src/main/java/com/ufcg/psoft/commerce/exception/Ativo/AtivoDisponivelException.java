package com.ufcg.psoft.commerce.exception.Ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AtivoDisponivelException extends CommerceException {
    public AtivoDisponivelException(){super("O ativo que você marcou interesse está disponível!");}
}
