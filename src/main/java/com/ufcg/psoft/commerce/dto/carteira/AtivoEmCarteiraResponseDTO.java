package com.ufcg.psoft.commerce.dto.carteira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.AtivoEmCarteira;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AtivoEmCarteiraResponseDTO {

    private Long ativoId;
    private String nomeAtivo;
    private TipoAtivo tipo;
    private Integer quantidadeTotal;
    private BigDecimal valorDeAquisicao;
    private BigDecimal valorAtual;
    private BigDecimal desempenho;

    public AtivoEmCarteiraResponseDTO(AtivoEmCarteira item) {
        Ativo temp_ativo = item.getAtivo();
        this.ativoId = temp_ativo.getId();
        this.nomeAtivo = temp_ativo.getNome();
        this.tipo = temp_ativo.getTipo();
        this.quantidadeTotal = item.getQuantidadeTotal();
        this.valorDeAquisicao = item.getValorDeAquisicao();
        this.valorAtual = temp_ativo.getCotacao();
        this.desempenho = item.getDesempenho();
    }
}
