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

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("cotacao")
    private String cotacao;

    @ManyToOne
    @JoinColumn(name = "tipo_id")
    @JsonProperty("tipo")
    private TipoAtivo tipo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("disponivel")
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