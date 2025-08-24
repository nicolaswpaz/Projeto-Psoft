package com.ufcg.psoft.commerce.dto.compra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("dataSolicitacao")
    private LocalDate dataSolicitacao;

    @JsonProperty("ativo")
    private Ativo ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorVenda")
    private BigDecimal valorVenda;

    @JsonProperty("conta")
    private Conta conta;

    @JsonProperty("status")
    private StatusCompra statusCompra;

    public CompraResponseDTO(Compra compra) {
        this.id = compra.getId();
        this.dataSolicitacao = compra.getDataSolicitacao();
        this.ativo = compra.getAtivo();
        this.quantidade = compra.getQuantidade();
        this.valorVenda = compra.getValorVenda();
        this.conta = compra.getConta();
        this.statusCompra = compra.getStatusCompra();
    }
}
