package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "operacao")
public abstract class Operacao {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @JsonProperty("data")
    private LocalDate dataSolicitacao;

    @ManyToOne
    @JsonProperty("ativo")
    private Ativo ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorVenda")
    private BigDecimal valorVenda;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public abstract void avancarStatus();

    @JsonProperty("statusAtual")
    public abstract String getStatusAtual();

    public BigDecimal getValorTotal() {
        return valorVenda.multiply(BigDecimal.valueOf(quantidade));
    }
}
