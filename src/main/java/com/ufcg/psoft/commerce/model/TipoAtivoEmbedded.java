package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ufcg.psoft.commerce.model.interfaces.TipoAtivo;
import jakarta.persistence.Embeddable;

@Embeddable
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Criptomoeda.class, name = "cripto"),
        @JsonSubTypes.Type(value = Acao.class, name = "acao"),
        @JsonSubTypes.Type(value = TesouroDireto.class, name = "tesouro")
})
public abstract class TipoAtivoEmbedded implements TipoAtivo {
    public String getTipo() {
        JsonTypeName annotation = this.getClass().getAnnotation(JsonTypeName.class);
        return annotation != null ? annotation.value() : null;
    }
}