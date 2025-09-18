package com.ufcg.psoft.commerce.events;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EventoAtivo {
    private Ativo ativo;
    private Cliente cliente;
}
