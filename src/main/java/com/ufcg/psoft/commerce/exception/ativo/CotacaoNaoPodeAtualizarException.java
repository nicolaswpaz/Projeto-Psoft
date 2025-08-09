package com.ufcg.psoft.commerce.exception.ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class CotacaoNaoPodeAtualizarException extends CommerceException {
    public CotacaoNaoPodeAtualizarException() {
        super("Somente ativos do tipo Acao ou Criptomoeda podem ter a cotacao atualizada");
    }
}
