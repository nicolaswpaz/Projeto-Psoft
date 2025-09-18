package com.ufcg.psoft.commerce.service.administrador;

import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorResponseDTO;

public interface AdministradorService {
    AdministradorResponseDTO criar(AdministradorPostPutRequestDTO administradorPostPutRequestDTO);

    AdministradorResponseDTO atualizarAdmin(AdministradorPostPutRequestDTO administradorPostPutRequestDTO, String matricula);

    void removerAdmin(String matricula);

    AdministradorResponseDTO buscarAdmin();

    void confirmarDisponibilidadeCompra(Long idCompra, String matricula);

    void confirmarResgate(Long idResgate, String matricula);
}
