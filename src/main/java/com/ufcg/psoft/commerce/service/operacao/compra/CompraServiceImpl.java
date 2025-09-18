package com.ufcg.psoft.commerce.service.operacao.compra;

import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.ativo.AtivoIndisponivelException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.exception.compra.QuantidadeInvalidaException;
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
import com.ufcg.psoft.commerce.service.autenticacao.AutenticacaoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService{

    private final CompraRepository compraRepository;
    private final ClienteService clienteService;
    private final InteresseCompraRepository interesseCompraRepository;
    private final AdministradorService administradorService;
    private final AutenticacaoService autenticacaoService;
    private final NotificacaoService notificacaoService;
    private final AtivoService ativoService;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    @Override
    public CompraResponseDTO solicitarCompra(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade) {
        Ativo ativo = ativoService.verificarAtivoExistente(idAtivo);
        Cliente cliente = autenticacaoService.autenticarCliente(idCliente, codigoAcesso);

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        if(quantidade < 1){
            throw new QuantidadeInvalidaException();
        }

        Compra compra = Compra.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
                .quantidade(quantidade)
                .valorVenda(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao()))
                .cliente(cliente)
                .build();

        compraRepository.save(compra);
        this.registrarInteresse(cliente, compra);
        return new CompraResponseDTO(compra);
    }

    @Override
    public CompraResponseDTO disponibilizarCompra(Long idCompra, String matriculaAdmin) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.SOLICITADO) {
            throw new StatusCompraInvalidoException();
        }

        if(Boolean.FALSE.equals(compra.getAtivo().isDisponivel())){
            throw new AtivoIndisponivelException();
        }

        administradorService.confirmarDisponibilidadeCompra(idCompra, matriculaAdmin);
        notificacaoService.notificarDisponibilidadeCompra(compra);
        return new CompraResponseDTO(compra);
    }

    @Override
    public CompraResponseDTO confirmarCompra(Long idCliente, String codigoAcesso, Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.DISPONIVEL) {
            throw new StatusCompraInvalidoException();
        }

        if(!compra.getCliente().getId().equals(idCliente)){
            throw new CompraNaoPertenceAoClienteException();
        }

        if(Boolean.FALSE.equals(compra.getAtivo().isDisponivel())){
            throw new AtivoIndisponivelException();
        }

        clienteService.confirmarCompraAtivo(idCliente, idCompra, codigoAcesso);
        return new CompraResponseDTO(compra);
    }

    @Override
    public CompraResponseDTO consultar(Long idCliente, String codigoAcesso, Long idCompra) {
        autenticacaoService.autenticarCliente(idCliente, codigoAcesso);

        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!compra.getCliente().getId().equals(cliente.getId())) {
            throw new CompraNaoPertenceAoClienteException();
        }

        return new CompraResponseDTO(compra);
    }

    @Override
    public List<CompraResponseDTO> listar(String matriculaAdmin) {
        autenticacaoService.autenticarAdmin(matriculaAdmin);

        return compraRepository.findAll().stream()
                .map(CompraResponseDTO::new)
                .collect(Collectors.toList());
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
