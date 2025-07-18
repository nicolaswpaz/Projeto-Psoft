package com.ufcg.psoft.commerce.service.administrador;

import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;

public interface AdministradorService {

    AdministradorResponseDTO criar(AdministradorPostPutRequestDTO adiministradorPostPutRequestDTO);


}
