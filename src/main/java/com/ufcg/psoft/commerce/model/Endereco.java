package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @JsonProperty("cep")
    @Column(nullable = false)
    private String cep;

    @JsonProperty("rua")
    @Column(nullable = false)
    private String rua;

    @JsonProperty("bairro")
    @Column(nullable = false)
    private String bairro;

    @JsonProperty("complemento")
    @Column(nullable = false)
    private String complemento;

    @JsonProperty("numero")
    @Column(nullable = false)
    private int numero;


}
