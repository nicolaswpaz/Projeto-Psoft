package com.ufcg.psoft.commerce.service.ativo.tipoativo;

import java.math.BigDecimal;

public abstract class TipoAtivoStrategy {

    private boolean podeAtualizarCotacao;

    protected TipoAtivoStrategy(boolean podeAtualizarCotacao) {
        this.podeAtualizarCotacao = podeAtualizarCotacao;
    }

    public boolean podeTerCotacaoAtualizada() {
        return this.podeAtualizarCotacao;
    }

    public abstract BigDecimal calculaImposto(BigDecimal lucro);
}
