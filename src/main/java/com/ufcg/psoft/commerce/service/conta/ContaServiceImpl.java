package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoGetRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.exception.Conta.ContaNaoExisteException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoListener;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class ContaServiceImpl implements ContaService {

    private static final Logger logger = LogManager.getLogger(ContaServiceImpl.class);

    @Autowired
    ContaRepository contaRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ClienteRepository clienteRepository;

    @Override
    public void adicionarAtivoNaListaDeInteresse(Long idConta, AtivoResponseDTO ativoIndisponivel) {

        Conta conta = contaRepository.findById(idConta).orElseThrow(ContaNaoExisteException::new);

        Ativo ativo = modelMapper.map(ativoIndisponivel, Ativo.class);

        if (conta.getAtivosDeInteresse() == null) {
            conta.setAtivosDeInteresse(new ArrayList<>());
        }
        if (!conta.getAtivosDeInteresse().contains(ativo)) {
            conta.getAtivosDeInteresse().add(ativo);
        }

        contaRepository.save(conta);
    }

    @Override
    public void notificarClientesComInteresse(Ativo ativoDisponivel) {
        List<Conta> contasInteressadas = contaRepository.findAll()
                .stream()
                .filter(conta -> conta.getAtivosDeInteresse() != null &&
                        conta.getAtivosDeInteresse().stream()
                                .anyMatch(a -> a.getId().equals(ativoDisponivel.getId())))
                .toList();

        if (!contasInteressadas.isEmpty()) {
            for (Conta conta : contasInteressadas) {
                AtivoGetRequestDTO dto = modelMapper.map(ativoDisponivel, AtivoGetRequestDTO.class);

                clienteRepository.findByContaId(conta.getId())
                        .ifPresent(cliente -> {
                            NotificacaoListener notificacao = new NotificacaoAtivoDisponivel();
                            notificacao.notificarAtivoDisponivel(cliente.getNome(), dto);
                        });

                conta.getAtivosDeInteresse().removeIf(a -> a.getId().equals(ativoDisponivel.getId()));
                contaRepository.save(conta);
            }
        } else {
            logger.info("Nenhuma conta com interesse no ativo '{}'", ativoDisponivel.getNome());
        }
    }

}
