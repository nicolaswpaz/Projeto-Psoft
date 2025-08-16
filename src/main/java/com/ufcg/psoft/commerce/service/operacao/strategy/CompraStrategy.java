package com.ufcg.psoft.commerce.service.operacao.strategy;

import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class CompraStrategy extends OperacaoStrategy {

    @Override
    public Operacao solicitar(Cliente cliente, Ativo ativo, int quantidade) {

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        Operacao compra = Operacao.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
                .quantidade(quantidade)
                .valorVenda(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao()))
                .cliente(cliente)
                .tipo(TipoOperacao.COMPRA)
                .statusCompra(StatusCompra.SOLICITADO)
                .build();

        return compra;
    }

    @Override
    public TipoOperacao getTipo() {
        return TipoOperacao.COMPRA;
    }
}

