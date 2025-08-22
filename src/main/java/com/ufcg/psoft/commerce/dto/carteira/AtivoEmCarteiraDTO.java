package com.ufcg.psoft.commerce.dto.carteira;

import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AtivoEmCarteiraDTO {

    private Long ativoId;
    private String nomeAtivo;
    private TipoAtivo tipo;
    private Integer quantidadeTotal;
    private BigDecimal valorTotalDeAquisicao;
    private BigDecimal valorTotalAtual;
    private BigDecimal desempenho;
}
