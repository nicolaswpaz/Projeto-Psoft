package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ativos")
public class Ativo {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nome;
    private String cotacao;

    @Embedded
    @JsonProperty("tipoAtivo")
    private TipoAtivoEmbedded tipoAtivo;

    private String descricao;
    private Boolean disponivel;

    @PrePersist
    public void setDefaultValues() {
        if (disponivel == null) {
            disponivel = true;
        }
    }

    public Boolean isDisponivel() {
        return disponivel;
    }
}