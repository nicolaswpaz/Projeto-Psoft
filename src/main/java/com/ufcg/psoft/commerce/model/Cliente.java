package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cliente")
public class Cliente extends Usuario {

    @Enumerated(EnumType.STRING)
    @JsonProperty("tipoPlano")
    private TipoPlano plano;

    @JsonIgnore
    @Column(nullable = false)
    private String codigo;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Conta conta;

    @PrePersist
    public void prePersist(){
        if (this.plano == null){
            this.plano = TipoPlano.NORMAL;
        }
    }
}
