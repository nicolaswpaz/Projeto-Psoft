package com.ufcg.psoft.commerce.service.itemcarteira;

import com.ufcg.psoft.commerce.dto.item.carteira.ItemCarteiraResponseDTO;
import com.ufcg.psoft.commerce.exception.itemcarteira.ItemCarteiraNaoExisteException;
import com.ufcg.psoft.commerce.model.ItemCarteira;
import com.ufcg.psoft.commerce.repository.ItemCarteiraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemCarteiraServiceImpl implements ItemCarteiraService {

    private final ItemCarteiraRepository itemCarteiraRepository;

    public ItemCarteiraServiceImpl(ItemCarteiraRepository itemCarteiraRepository) {
        this.itemCarteiraRepository = itemCarteiraRepository;
    }

    @Override
    public ItemCarteiraResponseDTO itemCarteiraUpdate(ItemCarteira item) {
        ItemCarteira existente = itemCarteiraRepository.findById(item.getId())
                .orElseThrow(ItemCarteiraNaoExisteException::new);

        existente.setQuantidadeTotal(item.getQuantidadeTotal());
        existente.setDesempenho(item.getDesempenho());

        ItemCarteira atualizado = itemCarteiraRepository.save(existente);
        return new ItemCarteiraResponseDTO(atualizado);
    }

    @Override
    public ItemCarteiraResponseDTO buscarPorId(Long id) {
        ItemCarteira item = itemCarteiraRepository.findById(id)
                .orElseThrow(ItemCarteiraNaoExisteException::new);
        return new ItemCarteiraResponseDTO(item);
    }

    @Override
    public List<ItemCarteiraResponseDTO> listarTodos() {
        return itemCarteiraRepository.findAll()
                .stream()
                .map(ItemCarteiraResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ItemCarteiraResponseDTO salvar(ItemCarteira item) {
        ItemCarteira salvo = itemCarteiraRepository.save(item);
        return new ItemCarteiraResponseDTO(salvo);
    }

    @Override
    public void remover(Long id) {
        if (!itemCarteiraRepository.existsById(id)) {
            throw new ItemCarteiraNaoExisteException();
        }
        itemCarteiraRepository.deleteById(id);
    }
}

