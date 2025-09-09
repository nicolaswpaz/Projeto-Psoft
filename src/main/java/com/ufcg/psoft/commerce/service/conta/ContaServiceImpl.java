package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.exception.conta.ValorDeSaldoInvalidoException;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;
    private final CompraRepository compraRepository;
    private final ModelMapper modelMapper;
    private final ClienteRepository clienteRepository;
    private final AtivoCarteiraRepository ativoCarteiraRepository;
    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    @Override
    public Conta criarContaPadrao() {
        Carteira carteira = new Carteira();

        Conta conta = Conta.builder()
                .saldo(BigDecimal.valueOf(0.00))
                .carteira(carteira)
                .build();

        return contaRepository.save(conta);
    }

    @Override
    public CompraResponseDTO confirmarCompra(Long idCliente, Long idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(CompraNaoExisteException::new);

        if (compra.getStatusCompra() != StatusCompra.DISPONIVEL) {
            throw new StatusCompraInvalidoException();
        }

        Conta conta = compra.getCliente().getConta();
        if (conta.getSaldo().compareTo(compra.getValorVenda()) < 0) {
            throw new SaldoInsuficienteException();
        }

        conta.setSaldo(conta.getSaldo().subtract(compra.getValorVenda()));
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        AtivoEmCarteira ativoEmCarteira = AtivoEmCarteira.builder()
                .quantidade(compra.getQuantidade())
                .ativo(compra.getAtivo())
                .valorDeAquisicao(compra.getValorVenda().divide(new BigDecimal(compra.getQuantidade()), SCALE, ROUNDING))
                .desempenho(BigDecimal.ZERO)
                .build();

        ativoCarteiraRepository.save(ativoEmCarteira);
        conta.getCarteira().getAtivosEmCarteira().add(ativoEmCarteira);
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        return modelMapper.map(compra, CompraResponseDTO.class);
    }

    @Override
    public List<AtivoEmCarteiraResponseDTO> visualizarCarteira(Long idCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        List<AtivoEmCarteira> carteira = cliente.getConta().getCarteira().getAtivosEmCarteira();

        return carteira.stream()
                .map(ativoEmCarteira -> {
                    Ativo ativo = ativoEmCarteira.getAtivo();

                    Integer quantidadeTotal = ativoEmCarteira.getQuantidade();

                    BigDecimal valorDeAquisicao = ativoEmCarteira.getValorDeAquisicao();

                    BigDecimal valorAtual = ativo.getCotacao();

                    BigDecimal desempenho = valorAtual.subtract(valorDeAquisicao);

                    return AtivoEmCarteiraResponseDTO.builder()
                            .id(ativo.getId())
                            .ativo(new AtivoResponseDTO(ativo))
                            .quantidade(quantidadeTotal)
                            .valorDeAquisicao(valorDeAquisicao)
                            .desempenho(desempenho)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void acrecentaSaldoConta(Long idCliente, BigDecimal valor) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValorDeSaldoInvalidoException();
        }

        Conta conta = cliente.getConta();
        BigDecimal novoSaldo = conta.getSaldo().add(valor);
        conta.setSaldo(novoSaldo);

        contaRepository.save(conta);
    }


}
