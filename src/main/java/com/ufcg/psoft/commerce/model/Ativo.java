package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ativos")
public class Ativo {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("cotacao")
    private String cotacao;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("disponivel")
    private Boolean disponivel;

    @Transient
    private AtivoStrategy tipoAtivo;

    @PrePersist
    public void setDefaultValues() {
        if (disponivel == null) {
            disponivel = true;
        }
    }

    public Boolean isDisponivel(){
        return disponivel;
    }

    public String getTipoAtivo() {
        if (this.tipoAtivo != null) {
            return this.tipoAtivo.getNomeTipo();
        }
        return "Desconhecido";
    }
}
