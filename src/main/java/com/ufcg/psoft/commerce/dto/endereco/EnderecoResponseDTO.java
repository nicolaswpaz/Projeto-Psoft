package com.ufcg.psoft.commerce.dto.endereco;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoResponseDTO {

    @JsonProperty("id")
    @JsonIgnore
    private Long id;

    @NotBlank(message = "Rua obrigatoria")
    private String rua;

    @NotBlank(message = "Bairro obrigatorio")
    private String bairro;

    @NotBlank(message = "Numero obrigatorio")
    private String numero;

    @NotBlank(message = "CEP obrigatorio")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP invalido")
    private String cep;

    private String complemento;

    public EnderecoResponseDTO(Endereco endereco) {
        this.cep = endereco.getCep();
        this.rua = endereco.getRua();
        this.bairro = endereco.getBairro();
        this.complemento = endereco.getComplemento();
        this.numero = endereco.getNumero();
    }
}
