package com.ufcg.psoft.commerce.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPlano {
    NORMAL("Normal"),
    PREMIUM("Premium");

    private String descricao;

    TipoPlano(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }
}
