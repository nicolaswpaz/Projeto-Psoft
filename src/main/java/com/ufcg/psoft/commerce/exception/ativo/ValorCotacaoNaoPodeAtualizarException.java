package com.ufcg.psoft.commerce.exception.ativo;

import com.ufcg.psoft.commerce.exception.CommerceException;

public class ValorCotacaoNaoPodeAtualizarException extends CommerceException {
  public ValorCotacaoNaoPodeAtualizarException() {
    super("O valor da cotação deve ser maior que zero");
  }
}