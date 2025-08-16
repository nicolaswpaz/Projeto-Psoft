package com.ufcg.psoft.commerce.service.operacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.operacao.OperacaoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperacaoServiceImpl implements OperacaoService {

    @Autowired
    OperacaoRepository operacaoRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Operacao criarOperacaoCompra(Cliente cliente, Ativo ativo, int quantidade) {
        Operacao compra = Operacao.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
                .quantidade(quantidade)
                .valorVenda(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao()))
                .cliente(cliente)
                .tipo(TipoOperacao.COMPRA)
                .build();

        operacaoRepository.save(compra);
        return compra;
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
