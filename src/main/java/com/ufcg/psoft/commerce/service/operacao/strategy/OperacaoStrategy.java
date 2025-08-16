package com.ufcg.psoft.commerce.service.operacao.strategy;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;

public abstract class OperacaoStrategy {

    public abstract Operacao solicitar(Cliente cliente, Ativo ativo, int quantidade);

    public abstract TipoOperacao getTipo();
}
