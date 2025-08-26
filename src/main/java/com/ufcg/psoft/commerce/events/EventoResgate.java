package com.ufcg.psoft.commerce.events;

import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Resgate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EventoResgate {
    private Resgate resgate;
    private Cliente cliente;
}
