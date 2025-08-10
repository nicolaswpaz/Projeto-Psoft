package com.ufcg.psoft.commerce.service.administrador;

import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;

public interface AdministradorService {

    Administrador autenticar(String matricula);

    AdministradorResponseDTO criar(AdministradorPostPutRequestDTO administradorPostPutRequestDTO);

    AdministradorResponseDTO atualizarAdmin(AdministradorPostPutRequestDTO administradorPostPutRequestDTO, String matricula);

    void removerAdmin(String matricula);

    AdministradorResponseDTO buscarAdmin();
}
