package com.ufcg.psoft.commerce.service.carteira;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.model.AtivoEmCarteira;

import java.util.List;

public interface AtivoEmCarteiravService {

    AtivoEmCarteiraResponseDTO itemCarteiraUpdate(AtivoEmCarteira item);

    AtivoEmCarteiraResponseDTO buscarPorId(Long id);

    List<AtivoEmCarteiraResponseDTO> listarTodos();

    AtivoEmCarteiraResponseDTO salvar(AtivoEmCarteira item);

    void remover(Long id);

}
