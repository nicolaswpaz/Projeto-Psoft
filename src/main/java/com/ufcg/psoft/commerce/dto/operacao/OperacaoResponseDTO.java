package com.ufcg.psoft.commerce.dto.operacao;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Ativo ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("cliente")
    private Cliente cliente;

    @JsonProperty("tipoOperacao")
    private String tipoOperacao;

    public OperacaoResponseDTO(Operacao operacao) {
        this.id = operacao.getId();
        this.cliente = operacao.getCliente();
        this.ativo = operacao.getAtivo();
        this.dataSolicitacao = operacao.getDataSolicitacao();
        this.tipoOperacao = operacao.getTipoOperacao();
    }
}
