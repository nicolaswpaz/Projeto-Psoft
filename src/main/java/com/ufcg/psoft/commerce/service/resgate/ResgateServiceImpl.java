package com.ufcg.psoft.commerce.service.resgate;

import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;
import com.ufcg.psoft.commerce.exception.compra.QuantidadeInvalidaException;
import com.ufcg.psoft.commerce.exception.resgate.ClienteNaoPossuiEsseAtivoEmCarteiraException;
import com.ufcg.psoft.commerce.exception.resgate.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ResgateServiceImpl implements ResgateService {

    private final AtivoService ativoService;
    private final ClienteService clienteService;
    private final ResgateRepository resgateRepository;
    private final ModelMapper modelMapper;

    public ResgateServiceImpl(AtivoService ativoService, ClienteService clienteService, ResgateRepository resgateRepository, ModelMapper modelMapper) {
        this.ativoService = ativoService;
        this.clienteService = clienteService;
        this.resgateRepository = resgateRepository;
        this.modelMapper = modelMapper;
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

        Carteira carteiraCliente = cliente.getConta().getCarteira();
        checarSaldo(carteiraCliente, ativo, quantidade);

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
        return null;
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
