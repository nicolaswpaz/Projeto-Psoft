package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Acao.class, name = "ACAO"),
        @JsonSubTypes.Type(value = Criptomoeda.class, name = "CRIPTOMOEDA"),
        @JsonSubTypes.Type(value = TesouroDireto.class, name = "TESOURO_DIRETO")
})
public abstract class TipoAtivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private boolean podeAtualizarCotacao;

    protected TipoAtivo(boolean podeAtualizarCotacao) {
        this.podeAtualizarCotacao = podeAtualizarCotacao;
    }

    public boolean podeTerCotacaoAtualizada() {
        return this.podeAtualizarCotacao;
    }
}
