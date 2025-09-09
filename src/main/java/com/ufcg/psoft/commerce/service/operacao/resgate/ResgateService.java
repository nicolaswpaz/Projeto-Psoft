package com.ufcg.psoft.commerce.service.operacao.resgate;

import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;

public interface ResgateService {

    ResgateResponseDTO solicitarResgate(Long idCliente, String codigoAcesso, Long idAtivo, int quantidade);

    ResgateResponseDTO confirmarResgate(Long idResgate, String matriculaAdmin);

    ResgateResponseDTO consultar(Long idCliente, String codigoAcesso, Long idResgate);
}
