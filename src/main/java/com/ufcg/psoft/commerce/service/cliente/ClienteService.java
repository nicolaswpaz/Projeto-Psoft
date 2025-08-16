package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
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

    List<AtivoResponseDTO> listarAtivosDisponiveisPorPlano(Long id, String codigoAcesso);

    void marcarInteresseAtivoIndisponivel(Long id, String codigoAcesso, Long idAtivo);

    void marcarInteresseAtivoDisponivel(Long id, String codigoAcesso, Long idAtivo);

    AtivoResponseDTO visualizarDetalhesAtivo(Long id, String codigoAcesso, Long idAtivo);

    void confirmarCompraAtivo(Long idCliente, Long idCompra, String codigoAcesso);

    void adicionarAtivoNaCarteira(Long idCliente, String codigoAcesso, Long idCompra);
}
