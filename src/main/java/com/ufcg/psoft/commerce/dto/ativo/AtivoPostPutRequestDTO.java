package com.ufcg.psoft.commerce.dto.ativo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoPostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("cotacao")
    @NotNull(message = "Cotacao obrigatoria")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cotacao deve ser maior que zero")
    private BigDecimal cotacao;

    @JsonProperty("descricao")
    @NotBlank(message = "Descricao obrigatoria")
    private String descricao;

    @JsonProperty("tipo")
    private TipoAtivo tipo;

    @JsonProperty("disponivel")
    @NotNull(message = "Disponibilidade obrigatoria")
    private boolean disponivel;
}
