package com.ufcg.psoft.commerce.dto.administrador;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdministradorResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @JsonProperty("endereco")
    @NotBlank(message = "Endereco obrigatorio")
    private EnderecoResponseDTO endereco;

    public AdministradorResponseDTO(Administrador admin) {
        this.nome = admin.getNome();
        this.endereco = new EnderecoResponseDTO(admin.getEndereco());
    }
}