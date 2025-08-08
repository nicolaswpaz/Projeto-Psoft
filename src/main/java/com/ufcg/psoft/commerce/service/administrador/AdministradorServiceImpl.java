package com.ufcg.psoft.commerce.service.administrador;


import com.ufcg.psoft.commerce.dto.administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.administrador.AdminJaExisteException;
import com.ufcg.psoft.commerce.exception.administrador.AdminNaoExisteException;
import com.ufcg.psoft.commerce.exception.administrador.MatriculaInvalidaException;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import com.ufcg.psoft.commerce.repository.EnderecoRepository;

import java.util.List;


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

    @Override
    public Administrador autenticar(String matricula) {
        Administrador administrador = getAdmin();

        if (!administrador.getMatricula().equals(matricula)) {
            throw new MatriculaInvalidaException();
        }
        return administrador;
    }

    @Override
    public AdministradorResponseDTO criar(AdministradorPostPutRequestDTO dto) {

        List<Administrador> adminObj = administradorRepository.findAll();

        if(adminObj.size() == 0){
        
            Administrador admin = modelMapper.map(dto, Administrador.class);

            if (dto.getEnderecoDTO() != null) {
                Endereco endereco = modelMapper.map(dto.getEnderecoDTO(), Endereco.class);
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

    @Override
    public Administrador getAdmin() {
        return administradorRepository.findTopBy()
                .orElseThrow(() -> new AdminNaoExisteException());
    }

}
