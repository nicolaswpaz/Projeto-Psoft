package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interesse_ativo")
public class InteresseAtivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Ativo ativo;

    @Enumerated(EnumType.STRING)
    private TipoInteresse tipoInteresse;
}
