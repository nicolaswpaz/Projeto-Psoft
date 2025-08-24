package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "carteira")
public class Carteira {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany(mappedBy = "carteira", cascade = CascadeType.ALL)
    @JsonProperty("ativos")
    private List<AtivoEmCarteira> ativosEmCarteira;


    public Carteira() {
        this.ativosEmCarteira = new ArrayList<>();
    }
}