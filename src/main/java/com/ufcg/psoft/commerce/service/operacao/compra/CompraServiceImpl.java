package com.ufcg.psoft.commerce.service.operacao.compra;

import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompraServiceImpl implements CompraService{

    private final CompraRepository compraRepository;
    private final ClienteService clienteService;
    private final AdministradorService administradorService;
    private final AtivoService ativoService;
    private final ModelMapper modelMapper;

    public CompraServiceImpl(CompraRepository compraRepository,
                             ClienteService clienteService,
                             AdministradorService administradorService,
                             AtivoService ativoService,
                             ModelMapper modelMapper) {
        this.compraRepository = compraRepository;
        this.clienteService = clienteService;
        this.administradorService = administradorService;
        this.ativoService = ativoService;
        this.modelMapper = modelMapper;
    }

    @Override
    public CompraResponseDTO solicitarCompra(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade) {
        Ativo ativo = ativoService.verificarAtivoExistente(idAtivo);
        Cliente cliente = clienteService.autenticar(idCliente, codigoAcesso);

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        Compra compra = Compra.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
                .quantidade(quantidade)
                .valorVenda(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao()))
                .cliente(cliente)
                .build();

        compraRepository.save(compra);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO disponibilizarCompra(Long idCompra, String matriculaAdmin) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.SOLICITADO) {
            throw new StatusCompraInvalidoException();
        }

        administradorService.confirmarDisponibilidadeCompra(idCompra, matriculaAdmin);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO confirmarCompra(Long idCliente, String codigoAcesso, Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.DISPONIVEL) {
            throw new StatusCompraInvalidoException();
        }

        clienteService.confirmarCompraAtivo(idCliente, idCompra, codigoAcesso);
        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public CompraResponseDTO consultar(Long idCliente, String codigoAcesso, Long idCompra) {
        clienteService.autenticar(idCliente, codigoAcesso);

        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (!compra.getCliente().getId().equals(idCliente)) {
            throw new CompraNaoPertenceAoClienteException();
        }

        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public List<CompraResponseDTO> listar(String matriculaAdmin) {
        administradorService.autenticar(matriculaAdmin);

        return compraRepository.findAll().stream()
                .map(CompraResponseDTO::new)
                .collect(Collectors.toList());
    }
}
