package com.ufcg.psoft.commerce.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum TipoOperacao {
    COMPRA,
    RESGATE;

    @JsonCreator
    public static TipoOperacao fromString(String value) {
        return Arrays.stream(TipoOperacao.values())
                .filter(e -> e.name().equalsIgnoreCase(value.replace(" ", "_")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("TipoOperacao invalida: " + value));
    }
}
