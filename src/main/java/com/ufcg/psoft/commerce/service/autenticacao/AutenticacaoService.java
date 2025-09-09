package com.ufcg.psoft.commerce.service.autenticacao;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Cliente;

public interface AutenticacaoService {
    Administrador autenticarAdmin(String matricula);

    Cliente autenticarCliente(Long id, String codigoAcesso);
}
