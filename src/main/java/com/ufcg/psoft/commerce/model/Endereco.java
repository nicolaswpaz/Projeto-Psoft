package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "endereços")
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
    private String complemento;

    @JsonProperty("numero")
    @Column(nullable = false)
    private int numero;
}
