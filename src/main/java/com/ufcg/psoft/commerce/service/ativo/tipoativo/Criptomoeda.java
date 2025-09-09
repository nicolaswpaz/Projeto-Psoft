package com.ufcg.psoft.commerce.service.ativo.tipoativo;

import java.math.BigDecimal;

public class Criptomoeda extends TipoAtivoStrategy{
    public Criptomoeda() { super(true); }

    public BigDecimal calculaImposto(BigDecimal lucro){
        if (lucro.compareTo(BigDecimal.valueOf(5000)) <= 0) {
            return lucro.multiply(BigDecimal.valueOf(0.15));
        }
        return lucro.multiply(BigDecimal.valueOf(0.225));
    }
}
