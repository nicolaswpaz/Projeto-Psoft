package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "operacao")

public class Operacao {
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
    @JsonProperty("cliente")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @JsonProperty("tipo")
    private TipoOperacao tipo;
}
