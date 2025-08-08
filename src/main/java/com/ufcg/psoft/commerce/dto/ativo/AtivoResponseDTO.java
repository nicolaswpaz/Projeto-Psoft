package com.ufcg.psoft.commerce.dto.ativo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
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
public class AtivoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("cotacao")
    @NotBlank(message = "Cotacao obrigatoria")
    private String cotacao;

    @JsonProperty("tipo")
    @NotNull(message = "Tipo obrigatorio")
    private TipoAtivo tipo;

    @JsonProperty("descricao")
    @NotBlank(message = "Descricao obrigatoria")
    private String descricao;

    @JsonProperty("disponibilidade")
    @NotBlank(message = "Disponibilidade obrigatoria")
    private boolean disponivel;

    public AtivoResponseDTO(Ativo ativo){

        this.id = ativo.getId();
        this.nome = ativo.getNome();
        this.tipo = ativo.getTipo();
        this.descricao = ativo.getDescricao();
        this.disponivel = ativo.isDisponivel();
        this.cotacao = ativo.getCotacao();
    }
}
