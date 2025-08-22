package com.ufcg.psoft.commerce.service.carteira;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.exception.itemcarteira.ItemCarteiraNaoExisteException;
import com.ufcg.psoft.commerce.model.AtivoEmCarteira;
import com.ufcg.psoft.commerce.repository.ItemCarteiraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtivoEmCarteiraServiceImpl implements AtivoEmCarteiravService {

    private final ItemCarteiraRepository itemCarteiraRepository;

    public AtivoEmCarteiraServiceImpl(ItemCarteiraRepository itemCarteiraRepository) {
        this.itemCarteiraRepository = itemCarteiraRepository;
    }

    @Override
    public AtivoEmCarteiraResponseDTO itemCarteiraUpdate(AtivoEmCarteira item) {
        AtivoEmCarteira existente = itemCarteiraRepository.findById(item.getId())
                .orElseThrow(ItemCarteiraNaoExisteException::new);

        existente.setQuantidadeTotal(item.getQuantidadeTotal());
        existente.setDesempenho(item.getDesempenho());

        AtivoEmCarteira atualizado = itemCarteiraRepository.save(existente);
        return new AtivoEmCarteiraResponseDTO(atualizado);
    }

    @Override
    public AtivoEmCarteiraResponseDTO buscarPorId(Long id) {
        AtivoEmCarteira item = itemCarteiraRepository.findById(id)
                .orElseThrow(ItemCarteiraNaoExisteException::new);
        return new AtivoEmCarteiraResponseDTO(item);
    }

    @Override
    public List<AtivoEmCarteiraResponseDTO> listarTodos() {
        return itemCarteiraRepository.findAll()
                .stream()
                .map(AtivoEmCarteiraResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AtivoEmCarteiraResponseDTO salvar(AtivoEmCarteira item) {
        AtivoEmCarteira salvo = itemCarteiraRepository.save(item);
        return new AtivoEmCarteiraResponseDTO(salvo);
    }

    @Override
    public void remover(Long id) {
        if (!itemCarteiraRepository.existsById(id)) {
            throw new ItemCarteiraNaoExisteException();
        }
        itemCarteiraRepository.deleteById(id);
    }
}

