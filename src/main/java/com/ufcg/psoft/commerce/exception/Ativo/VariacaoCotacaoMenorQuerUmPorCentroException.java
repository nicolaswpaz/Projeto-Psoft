package com.ufcg.psoft.commerce.exception.Ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class VariacaoCotacaoMenorQuerUmPorCentroException extends CommerceException {
    public VariacaoCotacaoMenorQuerUmPorCentroException() {
        super("A variacao da cotacao deve ser de no minimo 1%");
    }
}
