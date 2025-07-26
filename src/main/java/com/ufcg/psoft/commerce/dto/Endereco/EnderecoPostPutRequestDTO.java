package com.ufcg.psoft.commerce.dto.Endereco;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoPostPutRequestDTO {

    @JsonProperty("cep")
    @NotBlank(message = "CEP obrigatorio")
    private String cep;

    @JsonProperty("rua")
    @NotBlank(message = "Rua obrigatoria")
    private String rua;

    @JsonProperty("bairro")
    @NotBlank(message = "Bairro obrigatorio")
    private String bairro;

    @JsonProperty("complemento")
    private String complemento;

    @JsonProperty("numero")
    @NotBlank(message = "Numero obrigatorio")
    private String numero;
}
