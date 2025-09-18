package com.ufcg.psoft.commerce.service.operacao.resgate;

import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;
import com.ufcg.psoft.commerce.exception.ativocarteira.AtivoCarteiraNaoExisteException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.QuantidadeInvalidaException;
import com.ufcg.psoft.commerce.exception.resgate.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.autenticacao.AutenticacaoService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ResgateServiceImpl implements ResgateService {

    private final AtivoService ativoService;
    private final AutenticacaoService autenticacaoService;
    private final ResgateRepository resgateRepository;
    private final AtivoCarteiraRepository ativoCarteiraRepository;
    private final ModelMapper modelMapper;
    private final ContaRepository contaRepository;
    private final AdministradorService administradorService;
    private final NotificacaoService notificacaoService;
    private final ClienteRepository clienteRepository;

    private void checarSaldo(Carteira carteira, Ativo ativo, int quantidade) {
        for (AtivoEmCarteira ativoEmCarteira : carteira.getAtivosEmCarteira()) {
            if (ativoEmCarteira.getAtivo().equals(ativo)) {
                if (quantidade > ativoEmCarteira.getQuantidade()) {
                    throw new SaldoInsuficienteException(
                            quantidade,
                            ativoEmCarteira.getQuantidade()
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
        Cliente cliente = autenticacaoService.autenticarCliente(idCliente, codigoAcesso);

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

        resgate.calculaLucro();
        resgateRepository.save(resgate);
        return new ResgateResponseDTO(resgate);
    }

    @Override
    public ResgateResponseDTO confirmarResgate(Long idResgate, String matriculaAdmin) {
        Resgate resgate = resgateRepository.findById(idResgate)
                .orElseThrow(ResgateNaoExisteException::new);

        if (resgate.getStatusResgate() != StatusResgate.SOLICITADO) {
            throw new StatusResgateInvalidoException();
        }

        administradorService.confirmarResgate(idResgate, matriculaAdmin);
        liquidarResgate(resgate);
        notificacaoService.notificarConfirmacaoResgate(resgate);
        return new ResgateResponseDTO(resgate);
    }

    private void liquidarResgate(Resgate resgate) {
        if (resgate.getStatusResgate() != StatusResgate.CONFIRMADO) {
            throw new StatusResgateInvalidoException();
        }

        Cliente cliente = resgate.getCliente();
        Carteira carteira = cliente.getConta().getCarteira();
        AtivoEmCarteira ativoCarteira = carteira.getAtivosEmCarteira().stream()
                .filter(aec -> aec.getAtivo().equals(resgate.getAtivo()))
                .findFirst()
                .orElseThrow(ClienteNaoPossuiEsseAtivoEmCarteiraException::new);
        checarSaldo(carteira, resgate.getAtivo(), resgate.getQuantidade());

        ativoCarteira.setQuantidade(ativoCarteira.getQuantidade() - resgate.getQuantidade());

        if(ativoCarteira.getQuantidade() <= 0) {
            carteira.getAtivosEmCarteira().remove(ativoCarteira);
            if (!ativoCarteiraRepository.existsById(ativoCarteira.getId())) {
                throw new AtivoCarteiraNaoExisteException();
            }
            ativoCarteiraRepository.deleteById(ativoCarteira.getId());
        }

        BigDecimal valorLiquido = resgate.getValorResgatado().subtract(resgate.getImposto());

        Conta conta = cliente.getConta();
        conta.setSaldo(conta.getSaldo().add(valorLiquido));
        contaRepository.save(conta);

        resgate.avancarStatus();
        resgateRepository.save(resgate);
    }

    @Override
    public ResgateResponseDTO consultar(Long idCliente, String codigoAcesso, Long idResgate) {
        autenticacaoService.autenticarCliente(idCliente, codigoAcesso);

        Resgate resgate = resgateRepository.findById(idResgate)
                .orElseThrow(ResgateNaoExisteException::new);

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!resgate.getCliente().getId().equals(cliente.getId())) {
            throw new ResgateNaoPertenceAoClienteException();
        }

        return new ResgateResponseDTO(resgate);
    }
}
