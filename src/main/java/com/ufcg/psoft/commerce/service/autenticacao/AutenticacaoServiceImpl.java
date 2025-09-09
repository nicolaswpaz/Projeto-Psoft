package com.ufcg.psoft.commerce.service.autenticacao;

import com.ufcg.psoft.commerce.exception.administrador.AdminNaoExisteException;
import com.ufcg.psoft.commerce.exception.administrador.MatriculaInvalidaException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.cliente.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoServiceImpl implements AutenticacaoService {
    private final AdministradorRepository administradorRepository;
    private final ClienteRepository clienteRepository;

    public Administrador autenticarAdmin(String matriculaAdmin) {
        Administrador administrador= administradorRepository.findTopBy()
                .orElseThrow(AdminNaoExisteException::new);

        if (!administrador.getMatricula().equals(matriculaAdmin)) {
            throw new MatriculaInvalidaException();
        }
        return administrador;
    }

    @Override
    public Cliente autenticarCliente(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
        return cliente;
    }
}

