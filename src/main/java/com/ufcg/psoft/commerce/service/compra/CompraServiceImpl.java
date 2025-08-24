package com.ufcg.psoft.commerce.service.compra;

import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.InteresseCompraRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CompraServiceImpl implements CompraService{

    private final CompraRepository compraRepository;
    private final ClienteService clienteService;
    private final InteresseCompraRepository interesseCompraRepository;
    private final AdministradorService administradorService;
    private final NotificacaoService notificacaoService;
    private final AtivoService ativoService;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public CompraServiceImpl(CompraRepository compraRepository,
                             ClienteService clienteService, InteresseCompraRepository interesseCompraRepository,
                             AdministradorService administradorService, NotificacaoServiceImpl notificacaoService,
                             AtivoService ativoService, ClienteRepository clienteRepository,
                             ModelMapper modelMapper) {
        this.compraRepository = compraRepository;
        this.clienteService = clienteService;
        this.interesseCompraRepository = interesseCompraRepository;
        this.administradorService = administradorService;
        this.notificacaoService = notificacaoService;
        this.ativoService = ativoService;
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CompraResponseDTO solicitarCompra(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade) {
        Ativo ativo = ativoService.verificarAtivoExistente(idAtivo);
        Cliente cliente = clienteService.autenticar(idCliente, codigoAcesso);
        Conta conta = cliente.getConta();

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        Compra compra = Compra.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
                .quantidade(quantidade)
                .valorVenda(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao()))
                .conta(conta)
                .build();

        compraRepository.save(compra);
        this.registrarInteresse(cliente, compra);
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
        notificacaoService.notificarDisponibilidadeCompra(compra);
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

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!compra.getConta().getId().equals(cliente.getConta().getId())) {
            throw new CompraNaoPertenceAoClienteException();
        }

        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public List<CompraResponseDTO> listar(String matriculaAdmin) {
        administradorService.autenticar(matriculaAdmin);

        return compraRepository.findAll().stream()
                .map(CompraResponseDTO::new)
                .toList();
    }

    @Override
    public void registrarInteresse(Cliente cliente, Compra compra) {
        InteresseCompra interesse = InteresseCompra.builder()
                .cliente(cliente)
                .compra(compra)
                .build();

        interesseCompraRepository.save(interesse);
    }
}
