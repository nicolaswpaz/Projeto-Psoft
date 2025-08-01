package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.model.Cliente;

import java.util.List;

public interface ClienteService {

    Cliente autenticar(Long id, String codigoAcesso);

    ClienteResponseDTO alterar(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO);

    ClienteResponseDTO recuperar(Long id, String codigoAcesso);

    ClienteResponseDTO criar(ClientePostPutRequestDTO clientePostPutRequestDTO);

    void remover(Long id, String codigoAcesso);

    List<ClienteResponseDTO> listarPorNome(String nome, String matriculaAdmin);

    List<ClienteResponseDTO> listar(String matriculaAdmin);

    List<AtivoResponseDTO> listarAtivosDisponiveisPorPlano(Long idCliente, String codigoAcesso);

    void marcarInteresseAtivoIndisponivel(Long idCliente, String codigoAcesso, Long idAtivo);
}
