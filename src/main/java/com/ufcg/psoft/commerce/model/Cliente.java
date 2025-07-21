package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.model.Endereco;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    @JsonProperty("id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JsonProperty("tipoPlano")
    private TipoPlano plano;

    @JsonProperty("nome")
    @Column(nullable = false)
    private String nome;

    @JsonIgnore
    @Column(nullable = false)
    private String codigo;

  //  Feature Futura
  //  @JsonIgnore
  //  private Conta conta = new Conta();
}
