package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;

import java.util.List;

public interface AtivoService {

    AtivoResponseDTO alterar(Long id, String codigoAcesso, AtivoPostPutRequestDTO clientePostPutRequestDTO);

    List<AtivoResponseDTO> listar();

    AtivoResponseDTO recuperar(Long id);

    AtivoResponseDTO criar(AtivoPostPutRequestDTO clientePostPutRequestDTO);

    void remover(Long id, String codigoAcesso);

    List<AtivoResponseDTO> listarPorNome(String nome);
}
