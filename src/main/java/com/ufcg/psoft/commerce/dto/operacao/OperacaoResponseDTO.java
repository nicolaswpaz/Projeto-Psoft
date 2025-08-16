package com.ufcg.psoft.commerce.dto.operacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperacaoResponseDTO {

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

    @JsonProperty("cliente")
    private Cliente cliente;

    @JsonProperty("tipo")
    private TipoOperacao tipo;

    public OperacaoResponseDTO(Operacao operacao) {
        this.id = operacao.getId();
        this.dataSolicitacao = operacao.getDataSolicitacao();
        this.ativo = operacao.getAtivo();
        this.quantidade = operacao.getQuantidade();
        this.valorVenda = operacao.getValorVenda();
        this.cliente = operacao.getCliente();
        this.tipo = operacao.getTipo();
    }
}
