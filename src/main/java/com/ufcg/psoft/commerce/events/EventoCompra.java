package com.ufcg.psoft.commerce.events;

import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Compra;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EventoCompra {
    private Compra compra;
    private Cliente cliente;
}
