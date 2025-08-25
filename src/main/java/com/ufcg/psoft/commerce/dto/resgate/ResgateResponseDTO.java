package com.ufcg.psoft.commerce.dto.resgate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Resgate;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
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
public class ResgateResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("dataSolicitacao")
    private LocalDate dataSolicitacao;

    @JsonProperty("ativo")
    private Ativo ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorResgastado")
    private BigDecimal valorResgatado;

    @JsonProperty("lucro")
    private BigDecimal lucro;

    @JsonProperty("imposto")
    private BigDecimal imposto;

    @JsonProperty("cliente")
    private Cliente cliente;

    @JsonProperty("status")
    private StatusResgate statusResgate;

    public ResgateResponseDTO(Resgate resgate) {
        this.id = resgate.getId();
        this.dataSolicitacao = resgate.getDataSolicitacao();
        this.ativo = resgate.getAtivo();
        this.quantidade = resgate.getQuantidade();
        this.valorResgatado = resgate.getValorResgatado();
        this.lucro = resgate.getLucro();
        this.imposto = resgate.getImposto();
        this.cliente = resgate.getCliente();
        this.statusResgate = resgate.getStatusResgate();
    }
}
