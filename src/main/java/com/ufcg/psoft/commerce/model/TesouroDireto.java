package com.ufcg.psoft.commerce.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TESOURO_DIRETO")
public class TesouroDireto extends TipoAtivo {
    public TesouroDireto() { super(false); }
}
