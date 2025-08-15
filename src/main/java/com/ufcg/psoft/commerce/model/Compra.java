package com.ufcg.psoft.commerce.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.tipoOperacao.TipoOperacaoStrategy;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compra extends TipoOperacaoStrategy {

    @Id
    private long id;

    @Override
    public String getEstado() {
        return "Retorna o estado";
    }
}

