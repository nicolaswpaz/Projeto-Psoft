package com.ufcg.psoft.commerce.exception.ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class AtivoNaoPodeMudarTipoException extends CommerceException {
    public AtivoNaoPodeMudarTipoException() {
        super("O ativo nao pode alterar o tipo");
    }
}

