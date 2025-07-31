package com.ufcg.psoft.commerce.dto.Ativo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("cotacao")
    @NotBlank(message = "Cotacao obrigatoria")
    private String cotacao;

    @JsonProperty("descricao")
    @NotBlank(message = "Descricao obrigatoria")
    private String descricao;

    @JsonProperty("tipo")
    private TipoAtivo tipo;

    @JsonProperty("disponivel")
    @NotNull(message = "Disponibilidade obrigatoria")
    private boolean disponivel;
}
