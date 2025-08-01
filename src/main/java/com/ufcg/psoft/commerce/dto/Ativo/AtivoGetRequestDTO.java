package com.ufcg.psoft.commerce.dto.Ativo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtivoGetRequestDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("Id")
    private Long id;

    @JsonProperty("Nome")
    private String nome;

    @JsonProperty("Tipo")
    private TipoAtivo tipo;

    @JsonProperty("cotacao")
    private Double cotacao;

    @JsonProperty("Descricao")
    private String descricao;

    @JsonProperty("ativo")
    private Ativo ativo;
}
