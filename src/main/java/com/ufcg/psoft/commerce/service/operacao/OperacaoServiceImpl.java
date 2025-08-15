package com.ufcg.psoft.commerce.service.operacao;

import com.ufcg.psoft.commerce.dto.Operacao.OperacaoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperacaoServiceImpl implements OperacaoService {

    @Autowired
    OperacaoRepository operacaoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public OperacaoResponseDTO criarOperacaoCompra(OperacaoPostPutRequestDTO operacaoPostPutRequestDTO) {
        Operacao compra = modelMapper.map(operacaoPostPutRequestDTO, Operacao.class);
        operacaoRepository.save(compra);
        return modelMapper.map(compra, OperacaoResponseDTO.class);
    }

    @Override
    public OperacaoResponseDTO buscarOperacaoCompra(Long id) {
        Operacao compra = operacaoRepository.findById(id)
                .orElseThrow(CompraNaoExisteException::new);

        return modelMapper.map(compra, OperacaoResponseDTO.class);
    }

    @Override
    public List<OperacaoResponseDTO> listarOperacoesCompras() {
        return operacaoRepository.findAll().stream()
                .filter(op -> op.getTipo() == TipoOperacao.COMPRA)
                .map(OperacaoResponseDTO::new)
                .collect(Collectors.toList());
    }
}
