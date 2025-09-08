package com.ufcg.psoft.commerce.dto.operacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Operacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperacaoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("dataSolicitacao")
    private LocalDate dataSolicitacao;

    @JsonProperty("ativo")
    private AtivoResponseDTO ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("cliente")
    private ClienteResponseDTO cliente;

    @JsonProperty("tipoOperacao")
    private String tipoOperacao;

    public OperacaoResponseDTO(Operacao operacao) {
        this.id = operacao.getId();
        this.cliente = new ClienteResponseDTO(operacao.getCliente());
        this.ativo = new AtivoResponseDTO((operacao.getAtivo()));
        this.dataSolicitacao = operacao.getDataSolicitacao();
        this.tipoOperacao = operacao.getTipoOperacao();
    }
}
