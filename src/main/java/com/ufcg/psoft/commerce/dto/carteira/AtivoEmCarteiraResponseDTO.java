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
    private Integer quantidade;
    private BigDecimal valorDeAquisicao;
    private BigDecimal valorAtual;
    private BigDecimal desempenho;

    public AtivoEmCarteiraResponseDTO(AtivoEmCarteira item) {
        Ativo tempAtivo = item.getAtivo();
        this.ativoId = tempAtivo.getId();
        this.nomeAtivo = tempAtivo.getNome();
        this.tipo = tempAtivo.getTipo();
        this.quantidade = item.getQuantidade();
        this.valorDeAquisicao = item.getValorDeAquisicao();
        this.valorAtual = tempAtivo.getCotacao();
        this.desempenho = item.getDesempenho();
    }
}
