package com.ufcg.psoft.commerce.model.enums;

public enum TipoAtivo {
    ACAO(true),
    CRIPTOMOEDA(true),
    TESOURO_DIRETO(false);

    private final boolean podeAtualizarCotacao;

    TipoAtivo(boolean podeAtualizarCotacao) {
        this.podeAtualizarCotacao = podeAtualizarCotacao;
    }

    public boolean podeTerCotacaoAtualizada(){
        return this.podeAtualizarCotacao;
    }
}
