package com.ufcg.psoft.commerce.exception.ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AtivoIndisponivelException extends CommerceException {
    public AtivoIndisponivelException(){super("O ativo está indisponível!");}
}
