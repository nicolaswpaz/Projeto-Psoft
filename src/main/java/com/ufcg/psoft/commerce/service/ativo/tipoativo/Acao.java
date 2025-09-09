package com.ufcg.psoft.commerce.service.ativo.tipoativo;

import java.math.BigDecimal;

public class Acao extends TipoAtivoStrategy{
    public Acao() { super(true); }

    public BigDecimal calculaImposto(BigDecimal lucro){
        return lucro.multiply(BigDecimal.valueOf(0.15));
    }
}
