package com.ufcg.psoft.commerce.service.itemcarteira;

import com.ufcg.psoft.commerce.dto.item.carteira.ItemCarteiraResponseDTO;
import com.ufcg.psoft.commerce.model.ItemCarteira;

import java.util.List;

public interface ItemCarteiraService {

    ItemCarteiraResponseDTO itemCarteiraUpdate(ItemCarteira item);

    ItemCarteiraResponseDTO buscarPorId(Long id);

    List<ItemCarteiraResponseDTO> listarTodos();

    ItemCarteiraResponseDTO salvar(ItemCarteira item);

    void remover(Long id);

}
