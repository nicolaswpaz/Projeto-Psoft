package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.service.operacao.compra.status.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compra")
public class Compra extends Operacao{

    @Enumerated(EnumType.STRING)
    private StatusCompra statusCompra;

    @Transient
    private StatusCompraState statusState;

    @JsonProperty("valorVenda")
    private BigDecimal valorVenda;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<InteresseCompra> interesses;

    @PrePersist
    public void setDefaultValues() {
        if (statusCompra == null) {
            this.setStatusCompra(StatusCompra.SOLICITADO);
        }
    }

    public BigDecimal getValorAtivo() {
        return valorVenda.divide(BigDecimal.valueOf(this.getQuantidade()), RoundingMode.HALF_UP);
    }
    
    @PostLoad
    public void initStatusState() {
        if (statusCompra != null) {
            this.statusState = switch (statusCompra) {
                case SOLICITADO -> new SolicitadoState(this);
                case DISPONIVEL -> new DisponivelState(this);
                case COMPRADO -> new CompradoState(this);
                case EM_CARTEIRA -> new EmCarteiraState(this);
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
        return this.statusCompra != null ? this.statusCompra.name() : "INDEFINIDO";
    }

    @Override
    public String getTipoOperacao() {
        return "COMPRA";
    }
}
