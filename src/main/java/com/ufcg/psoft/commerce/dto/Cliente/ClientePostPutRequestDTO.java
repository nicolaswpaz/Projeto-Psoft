package com.ufcg.psoft.commerce.dto.Cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientePostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("endereco")
    @NotNull(message = "Endereco obrigatorio")
    @Valid
    private EnderecoResponseDTO enderecoDTO;

    @JsonProperty("codigo")
    @NotBlank(message = "Codigo de acesso obrigatorio")
    @Pattern(regexp = "^\\d{6}$", message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codigo;

    @JsonProperty("cpf")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    @NotBlank(message = "CPF obrigatorio")
    private String cpf;

    @JsonProperty("plano")
    private TipoPlano plano;
}
