package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ativo_carteira")
public class AtivoEmCarteira {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @JsonProperty("quantidade")
    private int quantidadeTotal;

    @JsonProperty("valor_de_aquisicao")
    private BigDecimal valorDeAquisicao;

    @ManyToOne
    @JsonProperty("ativo")
    private Ativo ativo;

    private BigDecimal desempenho;

    private BigDecimal cotacaoAtual;

    @Transient
    public BigDecimal getCotacaoAtual() {
        if (ativo == null || ativo.getCotacao() == null) {
            return BigDecimal.ZERO;
        }
        return ativo.getCotacao();
    }

    @Transient
    public BigDecimal getDesempenho() {
        if (valorDeAquisicao == null) {
            return BigDecimal.ZERO;
        }
        return getCotacaoAtual().subtract(valorDeAquisicao);
    }
}