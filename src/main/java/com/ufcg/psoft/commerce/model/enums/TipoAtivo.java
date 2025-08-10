package com.ufcg.psoft.commerce.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum TipoAtivo {
    ACAO,
    CRIPTOMOEDA,
    TESOURO_DIRETO;

    @JsonCreator
    public static TipoAtivo fromString(String value) {
        return Arrays.stream(TipoAtivo.values())
                .filter(e -> e.name().equalsIgnoreCase(value.replace(" ", "_")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("TipoAtivo invalido: " + value));
    }
}
