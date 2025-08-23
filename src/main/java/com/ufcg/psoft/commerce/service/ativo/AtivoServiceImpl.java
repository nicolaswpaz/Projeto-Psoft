package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.exception.ativo.*;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.AtivoEmCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.InteresseAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.InteresseAtivoRepository;
import com.ufcg.psoft.commerce.repository.ItemCarteiraRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.tipoativo.Acao;
import com.ufcg.psoft.commerce.service.ativo.tipoativo.Criptomoeda;
import com.ufcg.psoft.commerce.service.ativo.tipoativo.TesouroDireto;
import com.ufcg.psoft.commerce.service.ativo.tipoativo.TipoAtivoStrategy;
import com.ufcg.psoft.commerce.service.conta.ContaService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AtivoServiceImpl implements AtivoService {

    @Autowired
    AtivoRepository ativoRepository;

    @Autowired
    InteresseAtivoRepository interesseAtivoRepository;

    @Autowired
    ItemCarteiraRepository itemCarteiraRepository;

    @Autowired
    AdministradorService administradorService;

    @Autowired
    ContaService contaService;

    @Autowired
    NotificacaoServiceImpl notificacaoService;

    @Autowired
    ModelMapper modelMapper;

    private final Map<TipoAtivo, TipoAtivoStrategy> tipoAtivoMap = Map.of(
            TipoAtivo.ACAO, new Acao(),
            TipoAtivo.TESOURO_DIRETO, new TesouroDireto(),
            TipoAtivo.CRIPTOMOEDA, new Criptomoeda()
    );

    @Override
    public AtivoResponseDTO criar(String matriculaAdmin, AtivoPostPutRequestDTO ativoPostPutRequestDTO) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = modelMapper.map(ativoPostPutRequestDTO, Ativo.class);
        ativoRepository.save(ativo);
        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }


    @Override
    public AtivoResponseDTO alterar(String matriculaAdmin, Long id, AtivoPostPutRequestDTO ativoPostPutRequestDTO) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        if (ativoPostPutRequestDTO.getTipo() != null
                && ativoPostPutRequestDTO.getTipo() != ativo.getTipo()) {
            throw new AtivoNaoPodeMudarTipoException();
        }

        if (ativoPostPutRequestDTO.getTipo() == null) {
            ativoPostPutRequestDTO.setTipo(ativo.getTipo());
        }

        modelMapper.map(ativoPostPutRequestDTO, ativo);
        ativoRepository.save(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public void remover(String matriculaAdmin, Long id) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        ativoRepository.delete(ativo);
    }

    @Override
    public AtivoResponseDTO recuperarDetalhado(Long id) {
        Ativo ativo = ativoRepository.findById(id)
                .orElseThrow(AtivoNaoExisteException::new);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public List<AtivoResponseDTO> listar() {
        List<Ativo> ativos = ativoRepository.findAll();
        return ativos.stream()
                .map(AtivoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoResponseDTO> listarPorNome(String nome) {
        List<Ativo> ativos = ativoRepository.findByNomeContaining(nome);
        return ativos.stream()
                .map(AtivoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AtivoResponseDTO tornarDisponivel(String matriculaAdmin, Long id) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        if(ativo.isDisponivel()) {
            throw new AtivoDisponivelException();
        }

        ativo.setDisponivel(true);

        ativoRepository.save(ativo);
        notificacaoService.notificarDisponibilidade(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO tornarIndisponivel(String matriculaAdmin, Long id) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        if(!ativo.isDisponivel()) {
            throw new AtivoIndisponivelException();
        }

        ativo.setDisponivel(false);
        ativoRepository.save(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO atualizarCotacao(String matriculaAdmin, Long id, BigDecimal valor) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        TipoAtivoStrategy tipoAtivoStrategy = tipoAtivoMap.get(ativo.getTipo());

        if (!(tipoAtivoStrategy.podeTerCotacaoAtualizada())) {
            throw new CotacaoNaoPodeAtualizarException();
        }

        BigDecimal valorAtual = ativo.getCotacao();

        BigDecimal diferenca = valor.subtract(valorAtual);
        BigDecimal variacaoPercentual = diferenca
                .divide(valorAtual, MathContext.DECIMAL64)
                .abs()
                .multiply(BigDecimal.valueOf(100));

        if (variacaoPercentual.compareTo(BigDecimal.valueOf(1.0)) < 0) {
            throw new VariacaoCotacaoMenorQuerUmPorCentroException();
        }

        ativo.setCotacao(valor);
        ativoRepository.save(ativo);

        List<AtivoEmCarteira> itensCarteiraAtualizados = itemCarteiraRepository.findByAtivoId(id);
        List<AtivoEmCarteiraResponseDTO> itensDTO = itensCarteiraAtualizados.stream()
                .map(AtivoEmCarteiraResponseDTO::new)
                .collect(Collectors.toList());

        if (variacaoPercentual.compareTo(BigDecimal.valueOf(10.0)) >= 0 && ativo.isDisponivel())
            notificacaoService.notificarVariacaoCotacao(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public List<AtivoResponseDTO> listarAtivosDisponiveis() {
        List<Ativo> ativos = ativoRepository.findByDisponivelTrue();

        return ativos.stream()
                .map(AtivoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Ativo verificarAtivoExistente(Long idAtivo) {
        return ativoRepository.findById(idAtivo)
                .orElseThrow(AtivoNaoExisteException::new);
    }

    @Override
    public void registrarInteresse(Cliente cliente, Ativo ativo, TipoInteresse tipoInteresse) {
        InteresseAtivo interesse = InteresseAtivo.builder()
                .cliente(cliente)
                .ativo(ativo)
                .tipoInteresse(tipoInteresse)
                .build();
        interesseAtivoRepository.save(interesse);
    }

}
