package com.ufcg.psoft.commerce.dto.Endereco;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Endereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponseDTO {
    @JsonProperty("cep")
    private String cep;

    @JsonProperty("rua")
    private String rua;

    @JsonProperty("bairro")
    private String bairro;

    @JsonProperty("complemento")
    private String complemento;

    @JsonProperty("numero")
    private int numero;

    public EnderecoResponseDTO(Endereco endereco) {
        this.cep = endereco.getCep();
        this.rua = endereco.getRua();
        this.bairro = endereco.getBairro();
        this.complemento = endereco.getComplemento();
        this.numero = endereco.getNumero();
    }
}
