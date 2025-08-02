package com.ufcg.psoft.commerce.service.conta;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Conta.ContaResponseDTO;
import com.ufcg.psoft.commerce.exception.Cliente.OperacaoNaoPermitidaException;
import com.ufcg.psoft.commerce.exception.Conta.ContaNaoExisteException;
import com.ufcg.psoft.commerce.exception.Conta.OperacaoInvalidaException;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.ContaRepository;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoVariouCotacao;
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
    public void adicionarAtivoNaListaDeInteresse(Long idConta, AtivoResponseDTO ativoDTO) {

        Conta conta = contaRepository.findById(idConta).orElseThrow(ContaNaoExisteException::new);

        Ativo ativo = modelMapper.map(ativoDTO, Ativo.class);

        if (conta.getAtivosDeInteresse() == null) {
            conta.setAtivosDeInteresse(new ArrayList<>());
        }
        if (!conta.getAtivosDeInteresse().contains(ativo)) {
            conta.getAtivosDeInteresse().add(ativo);
        }

        trataCasoAtivoDisponivel(ativo);
        contaRepository.save(conta);
    }

    private void trataCasoAtivoDisponivel(Ativo ativo){
        if(ativo.isDisponivel() && ativo.getTipo() == TipoAtivo.TESOURO_DIRETO){
            throw new OperacaoInvalidaException();
        }
    }

    private ContaResponseDTO notificarClientesComInteresse(Ativo ativoDisponivel, NotificacaoListener notificacao) {
        List<Conta> contas = contaRepository.findAll();

        for (Conta conta : contas) {
            List<Ativo> interesses = conta.getAtivosDeInteresse();

            if (interesses == null || interesses.stream().noneMatch(a -> a.getId().equals(ativoDisponivel.getId()))) {
                continue;
            }

            clienteRepository.findByContaId(conta.getId()).ifPresent(cliente -> {
                AtivoResponseDTO dto = modelMapper.map(ativoDisponivel, AtivoResponseDTO.class);
                notificacao.notificarCliente(cliente.getNome(), dto);
            });
        }
        return modelMapper.map(contas, ContaResponseDTO.class);
    }

    @Override
    public ContaResponseDTO notificarAtivoDisponivelClientesComInteresse(Ativo ativoDisponivel) {
        NotificacaoListener notificacao = new NotificacaoAtivoDisponivel();
        return notificarClientesComInteresse(ativoDisponivel, notificacao);
    }

    @Override
    public ContaResponseDTO notificarClientesPremiumComInteresse(Ativo ativo){
        NotificacaoListener notificacao = new NotificacaoAtivoVariouCotacao();
        return notificarClientesComInteresse(ativo, notificacao);
    }

}
