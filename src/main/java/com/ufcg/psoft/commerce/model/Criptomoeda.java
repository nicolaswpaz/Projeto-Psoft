package com.ufcg.psoft.commerce.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CRIPTOMOEDA")
public class Criptomoeda extends TipoAtivo {
    public Criptomoeda() { super(true); }
}
