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
import com.ufcg.psoft.commerce.model.Endereco;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    @Enumerated(EnumType.STRING)
    @JsonProperty("tipoPlano")
    private TipoPlano plano;

    @JsonIgnore
    @Column(nullable = false)
    private String codigo;

    @ManyToOne
    @JsonIgnore
    private Conta conta;

    @PrePersist
    public void prePersist(){
        if (this.plano == null){
            this.plano = TipoPlano.NORMAL;
        }
    }
}
