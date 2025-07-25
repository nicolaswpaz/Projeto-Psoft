package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.exception.Ativo.AtivoNaoExisteException;
import com.ufcg.psoft.commerce.model.Acao;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Criptomoeda;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtivoServiceImpl implements AtivoService {

    @Autowired
    AtivoRepository ativoRepository;

    @Autowired
    AdministradorService administradorService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AtivoResponseDTO criar(String matriculaAdmin, AtivoPostPutRequestDTO ativoPostPutRequestDTO) {
        Administrador admin = administradorService.autenticar(matriculaAdmin);

        Ativo ativo = modelMapper.map(ativoPostPutRequestDTO, Ativo.class);
        ativoRepository.save(ativo);
        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO alterar(String matriculaAdmin, Long id, AtivoPostPutRequestDTO ativoPostPutRequestDTO) {
        Administrador admin = administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        modelMapper.map(ativoPostPutRequestDTO, ativo);
        ativoRepository.save(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public void remover(String matriculaAdmin, Long id) {
        Administrador admin = administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);

        ativoRepository.delete(ativo);
    }

    @Override
    public AtivoResponseDTO recuperar(Long id) {
        Ativo ativo = ativoRepository.findById(id).orElseThrow(AtivoNaoExisteException::new);
        return new AtivoResponseDTO(ativo);
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
        Administrador admin = administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(ativoId).orElseThrow(AtivoNaoExisteException::new);

        ativo.setDisponivel(true);

        ativoRepository.save(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO tornarIndisponivel(String matriculaAdmin, Long ativoId) {
        Administrador admin = administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(ativoId).orElseThrow(AtivoNaoExisteException::new);

        ativo.setDisponivel(false);

        ativoRepository.save(ativo);

        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO atualizarCotacao(String matriculaAdmin, Long idAtivo, double valor) {
        Administrador admin = administradorService.autenticar(matriculaAdmin);

        Ativo ativo = ativoRepository.findById(idAtivo).orElseThrow(AtivoNaoExisteException::new);

        if (!(ativo.getTipoAtivo().equals("ACAO") || ativo.getTipoAtivo().equals("CRIPTOMOEDA"))) {
            throw new IllegalArgumentException("Somente ativos do tipo Ação ou Criptomoeda podem ter a cotação atualizada");
        }

        double valorAtual = Double.parseDouble(ativo.getCotacao());
        double variacaoPercentual = Math.abs((valor - valorAtual) / valorAtual) * 100;

        if (variacaoPercentual < 1.0) {
            throw new IllegalArgumentException("A variação da cotação deve ser de no mínimo 1%");
        }

        ativo.setCotacao(String.valueOf(valor));
        ativoRepository.save(ativo);

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
