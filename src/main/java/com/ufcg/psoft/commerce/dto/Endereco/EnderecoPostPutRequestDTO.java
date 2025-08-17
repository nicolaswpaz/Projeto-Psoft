package com.ufcg.psoft.commerce.dto.Endereco;

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
public class EnderecoPostPutRequestDTO {
    @NotBlank(message = "Rua obrigatória")
    private String rua;

    @NotBlank(message = "Bairro obrigatório")
    private String bairro;

    @NotBlank(message = "Número obrigatório")
    private String numero;

    @NotBlank(message = "CEP obrigatório")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "CEP inválido")
    private String cep;

    private String complemento;
}