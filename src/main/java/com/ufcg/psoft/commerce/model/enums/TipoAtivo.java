package com.ufcg.psoft.commerce.model.enums;

import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonValue;


public enum TipoAtivo {
    ACAO("Ação"),
    CRIPTOMOEDA("Criptomoeda"),
    TESOURO_DIRETO("Tesouro Direto");

    private final String descricao;

    TipoAtivo(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue // Indica que este método deve ser usado para serialização JSON
    public String getDescricao() {
        return descricao;
    }
}
