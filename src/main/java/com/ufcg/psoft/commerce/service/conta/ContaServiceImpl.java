package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Conta.ContaResponseDTO;
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

@Service
public class ContaServiceImpl implements ContaService {

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
    public ContaResponseDTO notificarClientesComInteresse(Ativo ativoDisponivel) {
        List<Conta> contas = contaRepository.findAll();

        for (Conta conta : contas) {
            List<Ativo> interesses = conta.getAtivosDeInteresse();

            if (interesses == null || interesses.stream().noneMatch(a -> a.getId().equals(ativoDisponivel.getId()))) {
                continue;
            }

            clienteRepository.findByContaId(conta.getId()).ifPresent(cliente -> {
                AtivoResponseDTO dto = modelMapper.map(ativoDisponivel, AtivoResponseDTO.class);
                NotificacaoListener notificacao = new NotificacaoAtivoDisponivel();
                notificacao.notificarAtivoDisponivel(cliente.getNome(), dto);
            });
        }
        return modelMapper.map(contas, ContaResponseDTO.class);
    }

}
