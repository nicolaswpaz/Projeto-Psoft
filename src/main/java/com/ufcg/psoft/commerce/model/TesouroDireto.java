package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.ufcg.psoft.commerce.model.interfaces.TipoAtivo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonTypeName("tesouro")
public class TesouroDireto implements TipoAtivo {

    @Override
    public boolean podeTerCotacaoAtualizada() {
        return false;
    }

    @Override
    public String getTipo() {
        return "tesouro";
    }
}
