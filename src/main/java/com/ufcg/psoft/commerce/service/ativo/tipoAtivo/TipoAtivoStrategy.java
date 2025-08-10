package com.ufcg.psoft.commerce.service.ativo.tipoAtivo;

public abstract class TipoAtivoStrategy {

    private boolean podeAtualizarCotacao;

    protected TipoAtivoStrategy(boolean podeAtualizarCotacao) {
        this.podeAtualizarCotacao = podeAtualizarCotacao;
    }

    public boolean podeTerCotacaoAtualizada() {
        return this.podeAtualizarCotacao;
    }
}
