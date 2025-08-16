package com.ufcg.psoft.commerce.service.administrador;

import com.ufcg.psoft.commerce.dto.administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.administrador.AdminJaExisteException;
import com.ufcg.psoft.commerce.exception.administrador.AdminNaoExisteException;
import com.ufcg.psoft.commerce.exception.administrador.MatriculaInvalidaException;
import com.ufcg.psoft.commerce.exception.compra.OperacaoNaoEUmaCompraException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.exception.operacao.OperacaoNaoExisteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoOperacao;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import com.ufcg.psoft.commerce.repository.EnderecoRepository;

import java.math.BigDecimal;
import java.util.List;


import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    OperacaoRepository operacaoRepository;

    @Override
    public Administrador autenticar(String matricula) {
        Administrador administrador = getAdmin();

        if (!administrador.getMatricula().equals(matricula)) {
            throw new MatriculaInvalidaException();
        }
        return administrador;
    }

    @Override
    public AdministradorResponseDTO criar(AdministradorPostPutRequestDTO administradorPostPutRequestDTO) {

        List<Administrador> adminObj = administradorRepository.findAll();

        if(adminObj.isEmpty()){
        
            Administrador admin = modelMapper.map(administradorPostPutRequestDTO, Administrador.class);

            if (administradorPostPutRequestDTO.getEnderecoDTO() != null) {
                Endereco endereco = modelMapper.map(administradorPostPutRequestDTO.getEnderecoDTO(), Endereco.class);
                endereco = enderecoRepository.save(endereco);
                admin.setEndereco(endereco);
        }

        Administrador adminSalvo = administradorRepository.save(admin);
        
        return modelMapper.map(adminSalvo, AdministradorResponseDTO.class);
        }

        throw new AdminJaExisteException();
    }

    @Override
    public AdministradorResponseDTO atualizarAdmin(AdministradorPostPutRequestDTO administradorPostPutRequestDTO, String matricula) {
        Administrador admin = autenticar(matricula);

        admin.setNome(administradorPostPutRequestDTO.getNome());
        admin.setCpf(administradorPostPutRequestDTO.getCpf());
        admin.setMatricula(administradorPostPutRequestDTO.getMatricula());

        if (administradorPostPutRequestDTO.getEnderecoDTO() != null){
            Endereco enderecoExistente = admin.getEndereco();
            modelMapper.map(administradorPostPutRequestDTO.getEnderecoDTO(), enderecoExistente);
        }

        administradorRepository.save(admin);
        return modelMapper.map(admin, AdministradorResponseDTO.class);
    }

    @Override
    public void removerAdmin(String matricula) {
        Administrador admin =  autenticar(matricula);

        administradorRepository.delete(admin);
    }

    private Administrador getAdmin() {
        return administradorRepository.findTopBy()
                .orElseThrow(() -> new AdminNaoExisteException());
    }

    @Override
    public AdministradorResponseDTO buscarAdmin() {
        Administrador admin = administradorRepository.findTopBy()
                .orElseThrow(AdminNaoExisteException::new);
        return modelMapper.map(admin, AdministradorResponseDTO.class);
    }

    @Override
    public void confirmarDisponibilidadeCompra(Long idCompra, String matricula) {
        Administrador admin = autenticar(matricula);

        Operacao operacao = operacaoRepository.findById(idCompra)
                .orElseThrow(OperacaoNaoExisteException::new);

        if (operacao.getTipo() != TipoOperacao.COMPRA) {
            throw new OperacaoNaoEUmaCompraException();
        }

        Cliente cliente = operacao.getCliente();
        Conta conta = cliente.getConta();

        BigDecimal valorCompra = operacao.getAtivo().getCotacao()
                .multiply(BigDecimal.valueOf(operacao.getQuantidade()));

        if (conta.getSaldo().compareTo(valorCompra) < 0) {
            throw new SaldoInsuficienteException();
        }

        operacao.avancarStatus();
        operacaoRepository.save(operacao);
    }
}
