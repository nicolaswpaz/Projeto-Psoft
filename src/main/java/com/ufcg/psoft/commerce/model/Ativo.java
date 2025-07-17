package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.interfaces.TipoAtivo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@Getter
@Setter
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

    @JsonProperty("tipoAtivo")
    private TipoAtivo tipoAtivo;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("disponivel")
    private boolean disponivel;
}
