package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ufcg.psoft.commerce.model.interfaces.TipoAtivo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("cripto")
public class Criptomoeda extends TipoAtivoEmbedded implements TipoAtivo {

    @Override
    public boolean podeTerCotacaoAtualizada() {
        return true;
    }

    @Override
    public String getTipo() {
        return "cripto";
    }
}
