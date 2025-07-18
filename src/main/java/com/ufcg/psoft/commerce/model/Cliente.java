package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("Plano")
    private Plano plano = new Plano();

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonProperty("endereco")
    @Column(nullable = false)
    private Endereco endereco;

    @JsonIgnore
    @Column(nullable = false)
    private String codigo;

    @JsonIgnore
    private Conta conta = new Conta();
}
