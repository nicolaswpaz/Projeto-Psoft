package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;

import java.util.List;

public interface AtivoService {

    AtivoResponseDTO criar(String matriculaAdmin, AtivoPostPutRequestDTO ativoPostPutRequestDTO);

    AtivoResponseDTO alterar(String matriculaAdmin, Long id, AtivoPostPutRequestDTO ativoPostPutRequestDTO);

    void remover(String matriculaAdmin, Long id);

    AtivoResponseDTO recuperarDetalhado (Long ativoId);

    List<AtivoResponseDTO> listar();

    List<AtivoResponseDTO> listarPorNome(String nome);

    AtivoResponseDTO tornarDisponivel(String matriculaAdmin, Long ativoId);

    AtivoResponseDTO tornarIndisponivel(String matriculaAdmin, Long ativoId);

    AtivoResponseDTO atualizarCotacao(String matriculaAdmin, Long idAtivo, double valor);

    List<AtivoResponseDTO> listarAtivosDisponiveis();
}
