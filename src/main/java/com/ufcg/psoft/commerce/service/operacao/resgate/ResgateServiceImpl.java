package com.ufcg.psoft.commerce.service.operacao.resgate;

import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.QuantidadeInvalidaException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.resgate.ClienteNaoPossuiEsseAtivoEmCarteiraException;
import com.ufcg.psoft.commerce.exception.resgate.ResgateNaoExisteException;
import com.ufcg.psoft.commerce.exception.resgate.ResgateNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.exception.resgate.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ResgateServiceImpl implements ResgateService {

    private final AtivoService ativoService;
    private final ClienteService clienteService;
    private final ResgateRepository resgateRepository;
    private final ModelMapper modelMapper;
    private final AdministradorService administradorService;
    private final NotificacaoService notificacaoService;
    private final ClienteRepository clienteRepository;

    public ResgateServiceImpl(AtivoService ativoService, ClienteService clienteService, ResgateRepository resgateRepository, ModelMapper modelMapper, AdministradorService administradorService, NotificacaoService notificacaoService, ClienteRepository clienteRepository) {
        this.ativoService = ativoService;
        this.clienteService = clienteService;
        this.resgateRepository = resgateRepository;
        this.modelMapper = modelMapper;
        this.administradorService = administradorService;
        this.notificacaoService = notificacaoService;
        this.clienteRepository = clienteRepository;
    }

    private void checarSaldo(Carteira carteira, Ativo ativo, int quantidade) {
        for (AtivoEmCarteira ativoEmCarteira : carteira.getAtivosEmCarteira()) {
            if (ativoEmCarteira.getAtivo().equals(ativo)) {
                if (quantidade > ativoEmCarteira.getQuantidadeTotal()) {
                    throw new SaldoInsuficienteException(
                            quantidade,
                            ativoEmCarteira.getQuantidadeTotal()
                    );
                }
                return;
            }
        }
        throw new ClienteNaoPossuiEsseAtivoEmCarteiraException();
    }

    @Override
    public ResgateResponseDTO solicitarResgate(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade) {
        Ativo ativo = ativoService.verificarAtivoExistente(idAtivo);
        Cliente cliente = clienteService.autenticar(idCliente, codigoAcesso);

        if (quantidade < 1) {
            throw new QuantidadeInvalidaException();
        }

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        Carteira carteira = cliente.getConta().getCarteira();
        checarSaldo(carteira, ativo, quantidade);

        Resgate resgate = Resgate.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
                .lucro(BigDecimal.ZERO)
                .imposto(BigDecimal.ZERO)
                .quantidade(quantidade)
                .valorResgatado(BigDecimal.valueOf(quantidade).multiply(ativo.getCotacao()))
                .cliente(cliente)
                .build();

        resgateRepository.save(resgate);
        return modelMapper.map(resgate, ResgateResponseDTO.class);
    }

    @Override
    public ResgateResponseDTO confirmarResgate(Long idResgate, String matriculaAdmin) {
        Resgate resgate = resgateRepository.findById(idResgate)
                .orElseThrow(ResgateNaoExisteException::new);

        administradorService.confirmarResgate(idResgate, matriculaAdmin);
        liquidarResgate(resgate);
        notificacaoService.notificarConfirmacacaoResgate(resgate);
        return modelMapper.map(resgate, ResgateResponseDTO.class);
    }

    private void liquidarResgate(Resgate resgate) {
        if (resgate.getStatusResgate() != StatusResgate.CONFIRMADO) {
            throw new StatusCompraInvalidoException();
        }

        Cliente cliente = resgate.getCliente();
        Carteira carteira = cliente.getConta().getCarteira();
        AtivoEmCarteira ativoCarteira = carteira.getAtivosEmCarteira().stream()
                .filter(aec -> aec.getAtivo().equals(resgate.getAtivo()))
                .findFirst()
                .orElseThrow(ClienteNaoPossuiEsseAtivoEmCarteiraException::new);

        ativoCarteira.setQuantidadeTotal(ativoCarteira.getQuantidadeTotal() - resgate.getQuantidade());

        if(ativoCarteira.getQuantidadeTotal() <= 0) {
            carteira.getAtivosEmCarteira().remove(ativoCarteira);
        }

        BigDecimal valorLiquido = resgate.getValorResgatado().subtract(resgate.getImposto());
        cliente.getConta().setSaldo(cliente.getConta().getSaldo().add(valorLiquido));

        resgate.avancarStatus();
        resgateRepository.save(resgate);
    }

    @Override
    public ResgateResponseDTO consultar(Long idCliente, String codigoAcesso, Long idResgate) {
        clienteService.autenticar(idCliente, codigoAcesso);

        Resgate resgate = resgateRepository.findById(idResgate)
                .orElseThrow(ResgateNaoExisteException::new);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!resgate.getCliente().getId().equals(cliente.getId())) {
            throw new ResgateNaoPertenceAoClienteException();
        }

        return modelMapper.map(resgate, ResgateResponseDTO.class);
    }

    @Override
    public List<ResgateResponseDTO> listar(String matriculaAdmin) {
        return List.of();
    }
}
