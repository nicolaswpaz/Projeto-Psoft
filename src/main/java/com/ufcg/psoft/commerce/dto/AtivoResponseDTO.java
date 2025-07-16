package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.interfaces.TipoAtivo;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoResponseDTO {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatório")
    private String nome;

    @JsonProperty("cotacao")
    @NotBlank(message = "Cotação obrigatória")
    private String cotacao;

    @JsonProperty("tipo")
    @NotBlank(message = "Tipo obrigatório")
    private TipoAtivo tipo;

    @JsonProperty("descricao")
    @NotBlank(message = "Descrição obrigatória")
    private String descricao;

    @JsonProperty("disponibilidade")
    @NotBlank(message = "Disponibilidade obrigatória")
    private boolean disponibilidade;

    public AtivoResponseDTO(Ativo ativo){
        this.id = ativo.getId();
    }
}
