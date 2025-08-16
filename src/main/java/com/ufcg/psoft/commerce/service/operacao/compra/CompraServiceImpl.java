package com.ufcg.psoft.commerce.service.operacao.compra;

import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Operacao;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.operacao.strategy.CompraStrategy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraServiceImpl implements CompraService{

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ContaRepository contaRepository;

    @Autowired
    OperacaoRepository operacaoRepository;

    @Autowired
    ClienteService clienteService;

    @Autowired
    AdministradorService administradorService;

    @Autowired
    AtivoService ativoService;

    @Autowired
    CompraStrategy compraStrategy;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CompraResponseDTO solicitarCompra(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade) {
        Ativo ativo = ativoService.verificarAtivoExistente(idAtivo);
        Cliente cliente = clienteService.autenticar(idCliente, codigoAcesso);

        Operacao compra = compraStrategy.solicitar(cliente, ativo, quantidade);

        operacaoRepository.save(compra);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO disponibilizarCompra(Long idCompra, String matriculaAdmin) {
        Operacao compra = operacaoRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.SOLICITADO) {
            throw new StatusCompraInvalidoException();
        }

        administradorService.confirmarDisponibilidadeCompra(idCompra, matriculaAdmin);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO confirmarCompra(Long idCliente, String codigoAcesso, Long idCompra) {
        Operacao compra = operacaoRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.DISPONIVEL) {
            throw new StatusCompraInvalidoException();
        }

        clienteService.confirmarCompraAtivo(idCliente, idCompra, codigoAcesso);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO adicionarNaCarteira(Long idCliente, String codigoAcesso, Long idCompra) {
        Operacao compra = operacaoRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.COMPRADO) {
            throw new StatusCompraInvalidoException();
        }

        clienteService.adicionarAtivoNaCarteira(idCliente, codigoAcesso, idCompra);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO consultar(Long idCliente, String codigoAcesso, Long idCompra) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        clienteService.autenticar(idCliente, codigoAcesso);

        Operacao operacao = operacaoRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (!operacao.getCliente().getId().equals(idCliente)) {
            throw new CompraNaoPertenceAoClienteException();
        }

        return modelMapper.map(operacao, CompraResponseDTO.class);
    }

    @Override
    public List<CompraResponseDTO> listar(String matriculaAdmin) {
        administradorService.autenticar(matriculaAdmin);

        return operacaoRepository.findAll().stream()
                .filter(op -> op.getTipo() == TipoOperacao.COMPRA)
                .map(CompraResponseDTO::new)
                .collect(Collectors.toList());
    }
}
