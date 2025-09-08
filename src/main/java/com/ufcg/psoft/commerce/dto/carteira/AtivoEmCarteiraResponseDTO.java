package com.ufcg.psoft.commerce.dto.carteira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.AtivoEmCarteira;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AtivoEmCarteiraResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("ativo")
    @NotBlank(message = "Ativo obrigatorio")
    private AtivoResponseDTO ativo;

    @JsonProperty("quantidade")
    @NotBlank(message = "Quantidade obrigatorio")
    private Integer quantidade;

    @JsonProperty("valorDeAquisicao")
    @NotBlank(message = "Valor de aquisicao obrigatorio")
    private BigDecimal valorDeAquisicao;

    @JsonProperty("desempenho")
    @NotBlank(message = "Desempenho obrigatorio")
    private BigDecimal desempenho;

    public AtivoEmCarteiraResponseDTO(AtivoEmCarteira item) {
        this.id = item.getId();
        this.ativo = new AtivoResponseDTO(item.getAtivo());
        this.quantidade = item.getQuantidade();
        this.valorDeAquisicao = item.getValorDeAquisicao();
        this.desempenho = item.getDesempenho();
    }
}
