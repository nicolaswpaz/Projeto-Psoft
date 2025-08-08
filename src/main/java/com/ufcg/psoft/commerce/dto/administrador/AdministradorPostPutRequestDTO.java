package com.ufcg.psoft.commerce.dto.administrador;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoPostPutRequestDTO;
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
public class AdministradorPostPutRequestDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("endereco")
    @NotNull(message = "Endereco obrigatorio")
    @Valid
    private EnderecoPostPutRequestDTO enderecoDTO;

    @JsonProperty("cpf")
    @NotBlank(message = "Cpf obrigatorio")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    private String cpf;

    @JsonProperty("matricula")
    @NotNull(message = "Matricula para acesso obrigatorio")
    private String matricula;
}
