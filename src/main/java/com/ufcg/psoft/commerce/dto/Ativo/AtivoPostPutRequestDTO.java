package com.ufcg.psoft.commerce.dto.Ativo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoPostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatório")
    private String nome;

    @JsonProperty("cotacao")
    @NotBlank(message = "Cotação obrigatória")
    private String cotacao;

    @JsonProperty("descricao")
    @NotBlank(message = "Descrição obrigatória")
    private String descricao;

    @JsonProperty("disponivel")
    @NotBlank(message = "Disponibilidade obrigatória")
    private boolean disponivel;
}
