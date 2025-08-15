package com.ufcg.psoft.commerce.dto.Operacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacaoPostPutRequestDTO {
    @JsonProperty("ativo")
    private Ativo ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorVenda")
    private BigDecimal valorVenda;

    @JsonProperty("cliente")
    private Cliente cliente;
}
