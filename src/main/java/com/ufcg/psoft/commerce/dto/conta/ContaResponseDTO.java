package com.ufcg.psoft.commerce.dto.conta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Conta;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContaResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("saldo")
    private BigDecimal saldo;

    public ContaResponseDTO(Conta conta) {
        this.id = conta.getId();
        this.saldo = conta.getSaldo();
    }
}