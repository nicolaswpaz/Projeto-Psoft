package com.ufcg.psoft.commerce.dto.compra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.model.Compra;
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
    private AtivoResponseDTO ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorVenda")
    private BigDecimal valorVenda;

    @JsonProperty("cliente")
    private ClienteResponseDTO cliente;

    @JsonProperty("status")
    private StatusCompra statusCompra;

    public CompraResponseDTO(Compra compra) {
        this.id = compra.getId();
        this.dataSolicitacao = compra.getDataSolicitacao();
        this.ativo = new AtivoResponseDTO(compra.getAtivo());
        this.quantidade = compra.getQuantidade();
        this.valorVenda = compra.getValorVenda();
        this.cliente = new ClienteResponseDTO(compra.getCliente());
        this.statusCompra = compra.getStatusCompra();
    }
}
