package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ativo")
public class Ativo {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("cotacao")
    private BigDecimal cotacao;

    @Enumerated(EnumType.STRING)
    @JsonProperty("tipo")
    private TipoAtivo tipo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("disponivel")
    private Boolean disponivel;

    @ManyToMany //nao gosto da ideia de ter duas listas
    @JsonProperty("clientesInteressados")
    private List<Cliente> clientesInteresseIndisponivel;

    @ManyToMany
    @JsonProperty("clientesInteressados")
    private List<Cliente> clientesInteresseDisponivel;


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