package com.ufcg.psoft.commerce.service.ativo.tipoativo;

import java.math.BigDecimal;

public class TesouroDireto extends TipoAtivoStrategy{
    public TesouroDireto() { super(false); }

    public BigDecimal calculaImposto(BigDecimal lucro){
        return lucro.multiply(BigDecimal.valueOf(0.10));
    }
}
