package com.ufcg.psoft.commerce.service.administrador;

import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;

public interface AdministradorService {

    AdministradorResponseDTO criar(AdministradorPostPutRequestDTO administradorPostPutRequestDTO);

    AdministradorResponseDTO atualizarAdmin(AdministradorPostPutRequestDTO administradorPostPutRequestDTO, String matricula);

    void removerAdmin(String matricula);

    Administrador getAdmin();
}
