package com.ufcg.psoft.commerce.service.administrador;


import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.Administrador.MatriculaInvalidaException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.Cliente.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Administrador autenticar(String matricula) {
        if (getAdmin()== null) {
            throw new IllegalStateException("Administrador não cadastrado no sistema.");
        }

        Administrador administrador = getAdmin();

        if (!administrador.getMatricula().equals(matricula)) {
            throw new MatriculaInvalidaException();
        } else {
            return administrador;
        }
    }

    @Override
    public AdministradorResponseDTO criar(AdministradorPostPutRequestDTO dto) {
        Administrador administradorExistente = getAdmin();

        if (administradorExistente != null) {
            throw new IllegalStateException("Já existe um administrador cadastrado no sistema.");
        }

        Administrador admin = modelMapper.map(dto, Administrador.class);
        Administrador adminSalvo = administradorRepository.save(admin);

        return modelMapper.map(adminSalvo, AdministradorResponseDTO.class);
    }

    @Override
    public AdministradorResponseDTO atualizarAdmin(AdministradorPostPutRequestDTO administradorPostPutRequestDTO, String matricula) {
        Administrador admin = autenticar(matricula);

        modelMapper.map(administradorPostPutRequestDTO, admin);
        administradorRepository.save(admin);
        return modelMapper.map(admin, AdministradorResponseDTO.class);
    }

    @Override
    public void removerAdmin(String matricula) {
        Administrador admin =  autenticar(matricula);

        administradorRepository.delete(admin);
    }

    @Override
    public Administrador getAdmin() {
        return administradorRepository.findTopBy()
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado."));
    }

}
