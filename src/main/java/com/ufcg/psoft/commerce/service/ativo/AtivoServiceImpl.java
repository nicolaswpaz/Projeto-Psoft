package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.exception.ativo.*;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.tipoAtivo.Acao;
import com.ufcg.psoft.commerce.service.ativo.tipoAtivo.Criptomoeda;
import com.ufcg.psoft.commerce.service.ativo.tipoAtivo.TesouroDireto;
import com.ufcg.psoft.commerce.service.ativo.tipoAtivo.TipoAtivoStrategy;
import com.ufcg.psoft.commerce.service.conta.ContaService;
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
    AdministradorService administradorService;

    @Autowired
    ContaService contaService;

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
    public AtivoResponseDTO recuperarDetalhado(Long idAtivo) {
        Ativo ativo = ativoRepository.findById(idAtivo)
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
    public AtivoResponseDTO tornarDisponivel(String matriculaAdmin, Long ativoId) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(ativoId).orElseThrow(AtivoNaoExisteException::new);

        if(ativo.isDisponivel()) {
            throw new AtivoDisponivelException();
        }

        ativo.setDisponivel(true);

        ativoRepository.save(ativo);

        contaService.notificarAtivoDisponivelClientesComInteresse(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO tornarIndisponivel(String matriculaAdmin, Long ativoId) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(ativoId).orElseThrow(AtivoNaoExisteException::new);

        if(!ativo.isDisponivel()) {
            throw new AtivoIndisponivelException();
        }

        ativo.setDisponivel(false);

        ativoRepository.save(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO atualizarCotacao(String matriculaAdmin, Long idAtivo, double valor) {
        administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(idAtivo).orElseThrow(AtivoNaoExisteException::new);

        TipoAtivoStrategy tipoAtivoStrategy = tipoAtivoMap.get(ativo.getTipo());

        if (!(tipoAtivoStrategy.podeTerCotacaoAtualizada())) {
            throw new CotacaoNaoPodeAtualizarException();
        }

        BigDecimal valorAtual = ativo.getCotacao();
        BigDecimal novoValor = BigDecimal.valueOf(valor);

        BigDecimal diferenca = novoValor.subtract(valorAtual);
        BigDecimal variacaoPercentual = diferenca
                .divide(valorAtual, MathContext.DECIMAL64)
                .abs()
                .multiply(BigDecimal.valueOf(100));

        if (variacaoPercentual.compareTo(BigDecimal.valueOf(1.0)) < 0) {
            throw new VariacaoCotacaoMenorQuerUmPorCentroException();
        }

        ativo.setCotacao(novoValor);
        ativoRepository.save(ativo);

        if (variacaoPercentual.compareTo(BigDecimal.valueOf(10.0)) > 0) {
            contaService.notificarClientesPremiumComInteresse(ativo);
        }

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public List<AtivoResponseDTO> listarAtivosDisponiveis() {
        List<Ativo> ativos = ativoRepository.findByDisponivelTrue();

        return ativos.stream()
                .map(AtivoResponseDTO::new)
                .collect(Collectors.toList());
    }
}
