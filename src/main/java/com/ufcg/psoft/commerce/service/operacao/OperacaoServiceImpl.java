package com.ufcg.psoft.commerce.service.operacao;

import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OperacaoServiceImpl implements OperacaoService {

    private final ClienteService clienteService;
    private final AdministradorService administradorService;

    public OperacaoServiceImpl(ClienteService clienteService, AdministradorService administradorService) {
        this.clienteService = clienteService;
        this.administradorService = administradorService;
    }

    @Override
    public List<OperacaoResponseDTO> consultarOperacaoComCLiente(Long idCliente, String codigoAcesso, String tipoAtivo, LocalDate dataInicio, LocalDate dataFim, String statusOperacao) {
        clienteService.autenticar(idCliente, codigoAcesso);
        return List.of();
    }

    @Override
    public List<OperacaoResponseDTO> consultarOperacoesComAdmin(String matriculaAdmin, Long idCliente, String tipoAtivo, LocalDate data, String tipoOperacao) {
        administradorService.autenticar(matriculaAdmin);
        return List.of();
    }
}
