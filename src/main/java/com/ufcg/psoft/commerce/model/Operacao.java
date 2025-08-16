package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import com.ufcg.psoft.commerce.service.operacao.compra.status.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "operacao")

public class Operacao {
    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @JsonProperty("data")
    private LocalDate dataSolicitacao;

    @ManyToOne
    @JsonProperty("ativo")
    private Ativo ativo;

    @JsonProperty("quantidade")
    private int quantidade;

    @JsonProperty("valorVenda")
    private BigDecimal valorVenda;

    @ManyToOne
    @JsonProperty("cliente")
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @JsonProperty("tipo")
    private TipoOperacao tipo;

    @Enumerated(EnumType.STRING)
    private StatusCompra statusCompra;

    @Enumerated(EnumType.STRING)
    private StatusResgate statusResgate;

    @Transient
    private StatusCompraState statusState;


    @PrePersist
    public void setDefaultValues() {
        if (tipo == TipoOperacao.COMPRA && statusCompra == null) {
            this.statusCompra = StatusCompra.SOLICITADO;
            this.statusState = new SolicitadoState(this);
        }
        // resgate
    }


    @PostLoad
    public void initStatusState() {
        if (tipo == TipoOperacao.COMPRA && statusCompra != null) {
            this.statusState = switch (statusCompra) {
                case SOLICITADO -> new SolicitadoState(this);
                case DISPONIVEL -> new DisponivelState(this);
                case COMPRADO -> new CompradoState(this);
                case EM_CARTEIRA -> new EmCarteiraState(this);
            };
        }
    }

    @JsonProperty("statusAtual")
    public String getStatusAtual() {
        if (statusState != null) return statusState.getNome();
        if (tipo == TipoOperacao.COMPRA && statusCompra != null) return statusCompra.name();
        if (tipo == TipoOperacao.RESGATE && statusResgate != null) return statusResgate.name();
        return "INDEFINIDO";
    }


    public void avancarStatus() {
        if (statusState != null) {
            statusState.mover();
        } else {
            initStatusState();
            if (statusState != null) statusState.mover();
        }
    }
}
