package com.ufcg.psoft.commerce.service.operacao;

import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Operacao;
import java.util.List;

public interface OperacaoService {

    Operacao criarOperacaoCompra(Cliente cliente, Ativo ativo, int quantidade);

    OperacaoResponseDTO buscarOperacaoCompra(Long id);

    List<OperacaoResponseDTO> listarOperacoesCompras();
}
