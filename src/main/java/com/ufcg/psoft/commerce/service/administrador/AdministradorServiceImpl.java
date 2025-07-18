package com.ufcg.psoft.commerce.service.administrador;


import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public AdministradorResponseDTO criar(AdministradorPostPutRequestDTO dto) {
        Optional<Administrador> adminExistente = administradorRepository.findTopBy();
        if (adminExistente.isPresent()) {
            throw new IllegalStateException("Já existe um administrador cadastrado no sistema.");
        }

        Administrador novoAdmin = Administrador.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .matricula(dto.getMatricula())
                .build();

        Administrador adminSalvo = administradorRepository.save(novoAdmin);
        return new AdministradorResponseDTO(adminSalvo);
    }

    @Override
    public Administrador atualizarAdmin(AdministradorPostPutRequestDTO dto, String matricula) {
        Administrador admin = administradorRepository.findTopBy()
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado."));

        if (!admin.getMatricula().equals(matricula)) {
            throw new IllegalArgumentException("Matrícula incorreta.");
        }

        admin.setNome(dto.getNome());
        admin.setCpf(dto.getCpf());
        admin.setMatricula(dto.getMatricula());

        return administradorRepository.save(admin);
    }

    @Override
    public void removerAdmin(String matricula) {
        Administrador admin = administradorRepository.findByMatricula(matricula)
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado com a matrícula fornecida."));

        administradorRepository.delete(admin);
    }

    @Override
    public Administrador getAdmin() {
        return administradorRepository.findTopBy()
                .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado."));
    }

}
