package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("saldo")
    private BigDecimal saldo;

    @ManyToMany
    @JsonProperty("ativosDeInteresse")
    private List<Ativo> ativosDeInteresse;

    @OneToMany
    @JsonProperty("cateira")
    private List<AtivoEmCarteira> carteira;

    @ManyToMany
    @JsonProperty("operacoes")
    private List<Operacao> operacoes;
}
