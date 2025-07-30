package com.ufcg.psoft.commerce.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ACAO")
public class Acao extends TipoAtivo {
    public Acao() { super(true); }
}
