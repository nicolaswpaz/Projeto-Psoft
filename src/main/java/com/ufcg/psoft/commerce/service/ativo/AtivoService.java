package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface AtivoService {

    AtivoResponseDTO criar(String matriculaAdmin, AtivoPostPutRequestDTO ativoPostPutRequestDTO);

    AtivoResponseDTO alterar(String matriculaAdmin, Long id, AtivoPostPutRequestDTO ativoPostPutRequestDTO);

    void remover(String matriculaAdmin, Long id);

    AtivoResponseDTO recuperarDetalhado (Long id);

    List<AtivoResponseDTO> listar();

    List<AtivoResponseDTO> listarPorNome(String nome);

    AtivoResponseDTO tornarDisponivel(String matriculaAdmin, Long id);

    AtivoResponseDTO tornarIndisponivel(String matriculaAdmin, Long id);

    AtivoResponseDTO atualizarCotacao(String matriculaAdmin, Long id, BigDecimal valor);

    List<AtivoResponseDTO> listarAtivosDisponiveis();
}
