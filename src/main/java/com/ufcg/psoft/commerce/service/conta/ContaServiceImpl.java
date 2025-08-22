package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.conta.ContaNaoExisteException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.ItemCarteira;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.repository.ItemCarteiraRepository;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.conta.notificacao.Notificacao;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoVariouCotacao;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;
    private final CompraRepository compraRepository;
    private final ModelMapper modelMapper;
    private final ClienteRepository clienteRepository;
    private final ItemCarteiraRepository itemCarteiraRepository;

    public ContaServiceImpl(ContaRepository contaRepository,
                            CompraRepository compraRepository,
                            ModelMapper modelMapper,
                            ClienteRepository clienteRepository, ItemCarteiraRepository itemCarteiraRepository) {
        this.contaRepository = contaRepository;
        this.compraRepository = compraRepository;
        this.modelMapper = modelMapper;
        this.clienteRepository = clienteRepository;
        this.itemCarteiraRepository = itemCarteiraRepository;
    }

    @Autowired
    ClienteService clienteService;

    @Override
    public Conta criarContaPadrao() {
        Conta conta = Conta.builder()
                .saldo(BigDecimal.valueOf(0.00))
                .ativosDeInteresse(new ArrayList<>())
                .carteira(new ArrayList<>())
                .build();

        return contaRepository.save(conta);
    }

    @Override
    public void adicionarAtivoNaListaDeInteresse(Long id, Ativo ativo) {

        Conta conta = contaRepository.findById(id).orElseThrow(ContaNaoExisteException::new);

        if (conta.getAtivosDeInteresse() == null) {
            conta.setAtivosDeInteresse(new ArrayList<>());
        }
        if (!conta.getAtivosDeInteresse().contains(ativo)) {
            conta.getAtivosDeInteresse().add(ativo);
        }

        contaRepository.save(conta);
    }

    private void notificarClientesComInteresse(Ativo ativo, Notificacao notificacao) {
        List<Conta> contas = contaRepository.findAll();

        for (Conta conta : contas) {
            List<Ativo> interesses = conta.getAtivosDeInteresse();

            if (interesses == null || interesses.stream().noneMatch(a -> a.getId().equals(ativo.getId()))) {
                continue;
            }

            clienteRepository.findByContaId(conta.getId()).ifPresent(cliente -> {
                AtivoResponseDTO dto = modelMapper.map(ativo, AtivoResponseDTO.class);
                notificacao.notificarCliente(cliente.getNome(), dto);
            });
        }
    }

    @Override
    public void notificarAtivoDisponivelClientesComInteresse(Ativo ativo) {
        notificarClientesComInteresse(ativo, new NotificacaoAtivoDisponivel());
    }

    @Override
    public void notificarClientesPremiumComInteresse(Ativo ativo){
        notificarClientesComInteresse(ativo, new NotificacaoAtivoVariouCotacao());
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

        ItemCarteira item = new ItemCarteira();
        Ativo ativo = compra.getAtivo();
        item.setAtivo(ativo);
        item.setQuantidadeTotal(compra.getQuantidade());
        item.setValorDeAquisicao(ativo.getCotacao());
        item.setDesempenho(new BigDecimal(0));
        item.setValorAtual(ativo.getCotacao());
        item.setConta(conta);

        itemCarteiraRepository.save(item);
        conta.getCarteira().add(item);
        contaRepository.save(conta);

        compra.avancarStatus();
        compraRepository.save(compra);

        return modelMapper.map(compra, CompraResponseDTO.class);
    }
    @Override
    public List<AtivoEmCarteiraDTO> visualizarCarteira(Long idCliente, String codigoAcesso) {

        clienteService.autenticar(idCliente, codigoAcesso);

        List<Compra> comprasEmCarteira = compraRepository.findAllByClienteIdAndStatusCompra(
                idCliente,
                StatusCompra.EM_CARTEIRA
        );

        Map<Ativo, List<Compra>> comprasAgrupadasPorAtivo = comprasEmCarteira.stream()
                .collect(Collectors.groupingBy(Compra::getAtivo));

        List<AtivoEmCarteiraDTO> carteiraConsolidada = comprasAgrupadasPorAtivo.entrySet().stream()
                .map(entry -> {
                    Ativo ativo = entry.getKey();
                    List<Compra> comprasDoAtivo = entry.getValue();

                    int quantidadeTotal = comprasDoAtivo.stream()
                            .mapToInt(Compra::getQuantidade)
                            .sum();

                    BigDecimal valorTotalAquisicao = comprasDoAtivo.stream()
                            .map(Compra::getValorVenda)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal valorTotalAtual = new BigDecimal(quantidadeTotal)
                            .multiply(ativo.getCotacao());

                    BigDecimal desempenho = valorTotalAtual.subtract(valorTotalAquisicao);

                    return AtivoEmCarteiraDTO.builder()
                            .ativoId(ativo.getId())
                            .nomeAtivo(ativo.getNome())
                            .tipo(ativo.getTipo())
                            .quantidadeTotal(quantidadeTotal)
                            .valorTotalDeAquisicao(valorTotalAquisicao)
                            .valorTotalAtual(valorTotalAtual)
                            .desempenho(desempenho)
                            .build();
                })
                .collect(Collectors.toList());

        return carteiraConsolidada;
    }

}
