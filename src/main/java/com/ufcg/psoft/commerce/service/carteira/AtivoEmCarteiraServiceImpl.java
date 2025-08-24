package com.ufcg.psoft.commerce.service.carteira;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.exception.ativocarteira.AtivoCarteiraNaoExisteException;
import com.ufcg.psoft.commerce.model.AtivoEmCarteira;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtivoEmCarteiraServiceImpl implements AtivoEmCarteiravService {

    private final AtivoCarteiraRepository ativoCarteiraRepository;

    public AtivoEmCarteiraServiceImpl(AtivoCarteiraRepository ativoCarteiraRepository) {
        this.ativoCarteiraRepository = ativoCarteiraRepository;
    }

    @Override
    public AtivoEmCarteiraResponseDTO buscarPorId(Long id) {
        AtivoEmCarteira ativoEmCarteira = ativoCarteiraRepository.findById(id)
                .orElseThrow(AtivoCarteiraNaoExisteException::new);
        return new AtivoEmCarteiraResponseDTO(ativoEmCarteira);
    }

    @Override
    public List<AtivoEmCarteiraResponseDTO> listarTodos() {
        return ativoCarteiraRepository.findAll()
                .stream()
                .map(AtivoEmCarteiraResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AtivoEmCarteiraResponseDTO salvar(AtivoEmCarteira ativoEmCarteira) {
        AtivoEmCarteira salvo = ativoCarteiraRepository.save(ativoEmCarteira);
        return new AtivoEmCarteiraResponseDTO(salvo);
    }

    @Override
    public void remover(Long id) {
        if (!ativoCarteiraRepository.existsById(id)) {
            throw new AtivoCarteiraNaoExisteException();
        }
        ativoCarteiraRepository.deleteById(id);
    }


}

