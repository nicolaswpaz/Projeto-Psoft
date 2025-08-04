package com.ufcg.psoft.commerce.exception.Ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AtivoIndisponivelException extends CommerceException {
    public AtivoIndisponivelException(){super("O ativo que você marcou interesse está indisponível!");}
}
