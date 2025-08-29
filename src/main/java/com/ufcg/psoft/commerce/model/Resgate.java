package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.exception.resgate.ClienteNaoPossuiEsseAtivoEmCarteiraException;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.service.operacao.resgate.status.SolicitadoState;
import com.ufcg.psoft.commerce.service.operacao.resgate.status.StatusResgateState;
import com.ufcg.psoft.commerce.service.operacao.resgate.status.ConfirmadoState;
import com.ufcg.psoft.commerce.service.operacao.resgate.status.EmContaState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resgate")
public class Resgate extends Operacao{

    @Enumerated(EnumType.STRING)
    private StatusResgate statusResgate;

    @Transient
    private StatusResgateState statusState;

    @JsonProperty("valorResgatado")
    private BigDecimal valorResgatado;

    @JsonProperty("lucro")
    private BigDecimal lucro;

    @JsonProperty("imposto")
    private BigDecimal imposto;

    @PrePersist
    public void setDefaultValues() {
        if (statusResgate == null) {
            this.setStatusResgate(StatusResgate.SOLICITADO);
        }
    }

    @PostLoad
    public void initStatusState() {
        if (statusResgate != null) {
            this.statusState = switch (statusResgate) {
                case SOLICITADO -> new SolicitadoState(this);
                case CONFIRMADO -> new ConfirmadoState(this);
                case EM_CONTA -> new EmContaState(this);
            };
        }
    }

    @Override
    public void avancarStatus() {
        if (this.statusState == null) {
            initStatusState();
        }
        this.statusState.mover();
    }

    @Override
    public String getStatusAtual() {
        if (this.statusState != null) {
            return this.statusState.getNome();
        }
        return this.statusResgate != null ? this.statusResgate.name() : "INDEFINIDO";
    }

    public void calculaLucro(){
        Cliente cliente = this.getCliente();
        Carteira carteira = cliente.getConta().getCarteira();

        AtivoEmCarteira ativoEmCarteira = carteira.getAtivosEmCarteira().stream()
                .filter(aec -> aec.getAtivo().getId().equals(this.getAtivo().getId()))
                .findFirst()
                .orElseThrow(() -> new ClienteNaoPossuiEsseAtivoEmCarteiraException());

        BigDecimal valorAtual = ativoEmCarteira.getAtivo().getCotacao()
                .multiply(BigDecimal.valueOf(this.getQuantidade()));
        BigDecimal valorCompraTotal = ativoEmCarteira.getValorDeAquisicao()
                .multiply(BigDecimal.valueOf(this.getQuantidade()));

        this.lucro = valorAtual.subtract(valorCompraTotal);
    }

    public void calculaImposto(){
        if (lucro.compareTo(BigDecimal.ZERO) <= 0) {
            imposto = BigDecimal.ZERO;
            return;
        }

        if (this.getAtivo().getTipo() == TipoAtivo.TESOURO_DIRETO) {
            imposto = lucro.multiply(BigDecimal.valueOf(0.10));
        } else if (this.getAtivo().getTipo() == TipoAtivo.ACAO) {
            imposto = lucro.multiply(BigDecimal.valueOf(0.15));
        } else if (this.getAtivo().getTipo() == TipoAtivo.CRIPTOMOEDA) {
            if (lucro.compareTo(BigDecimal.valueOf(5000)) <= 0) {
                imposto = lucro.multiply(BigDecimal.valueOf(0.15));
            } else {
                imposto = lucro.multiply(BigDecimal.valueOf(0.225));
            }
        }

        imposto = imposto.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getTipoOperacao() {
        return "RESGATE";
    }
}
