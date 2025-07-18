package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Usuario {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @Embedded
    @JsonProperty("endereco")
    private Endereco endereco;

    @JsonProperty("cpf")
    @Column(nullable = false)
    private String cpf;

    @JsonIgnore
    @Column(nullable = false)
    private String codigo;

}