package com.ufcg.psoft.commerce.service.resgate;

import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;
import com.ufcg.psoft.commerce.exception.ativo.AtivoIndisponivelException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.QuantidadeInvalidaException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.resgate.ClienteNaoPossuiEsseAtivoEmCarteiraException;
import com.ufcg.psoft.commerce.exception.resgate.ResgateNaoExisteException;
import com.ufcg.psoft.commerce.exception.resgate.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
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

    public ResgateServiceImpl(AtivoService ativoService, ClienteService clienteService, ResgateRepository resgateRepository, ModelMapper modelMapper, AdministradorService administradorService, NotificacaoService notificacaoService) {
        this.ativoService = ativoService;
        this.clienteService = clienteService;
        this.resgateRepository = resgateRepository;
        this.modelMapper = modelMapper;
        this.administradorService = administradorService;
        this.notificacaoService = notificacaoService;
    }

    private AtivoEmCarteira checarSaldo(Carteira carteira, Ativo ativo, int quantidade) {
        for (AtivoEmCarteira ativoEmCarteira : carteira.getAtivosEmCarteira()) {
            if (ativoEmCarteira.getAtivo().equals(ativo)) {
                if (quantidade > ativoEmCarteira.getQuantidadeTotal()) {
                    throw new SaldoInsuficienteException(
                            quantidade,
                            ativoEmCarteira.getQuantidadeTotal()
                    );
                }
                return ativoEmCarteira;
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

        Resgate resgate = Resgate.builder()
                .dataSolicitacao(LocalDate.now())
                .ativo(ativo)
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

        if (resgate.getStatusResgate() != StatusResgate.SOLICITADO) {
            throw new StatusCompraInvalidoException();
        }

        if(Boolean.FALSE.equals(resgate.getAtivo().isDisponivel())){
            throw new AtivoIndisponivelException();
        }

        Carteira carteiraCliente = resgate.getCliente().getConta().getCarteira();
        checarSaldo(carteiraCliente, resgate.getAtivo(), resgate.getQuantidade());

        //cálculo do imposto correspondente.

        administradorService.confirmarResgate(idResgate, matriculaAdmin);
        notificacaoService.notificarConfirmacacaoResgate(resgate);
        return modelMapper.map(resgate, ResgateResponseDTO.class);
    }

    @Override
    public ResgateResponseDTO consultar(Long idCliente, String codigoAcesso, Long idResgate) {
        return null;
    }

    @Override
    public List<ResgateResponseDTO> listar(String matriculaAdmin) {
        return List.of();
    }
}
