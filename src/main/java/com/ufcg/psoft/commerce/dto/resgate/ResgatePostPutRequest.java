package com.ufcg.psoft.commerce.dto.resgate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
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
public class ResgatePostPutRequest {
    @JsonProperty("ativo")
    private Ativo ativo;

    @JsonProperty("dataSolicitacao")
    private LocalDate dataSolicitacao;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorResgatado")
    private BigDecimal valorResgatado;

    @JsonProperty("cliente")
    private Cliente cliente;

    @JsonProperty("status")
    private StatusResgate statusResgate;
}
