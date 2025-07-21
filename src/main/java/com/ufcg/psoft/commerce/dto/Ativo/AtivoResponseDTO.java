package com.ufcg.psoft.commerce.dto.Ativo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
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
    private boolean disponivel;

    public AtivoResponseDTO(Ativo ativo){

        this.id = ativo.getId();
        this.nome = ativo.getNome();
        this.tipo = ativo.getTipoAtivo();
        this.descricao = ativo.getDescricao();
        this.disponivel = ativo.isDisponivel();
        this.cotacao = ativo.getCotacao();
    }
}
