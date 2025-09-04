package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Operacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperacaoRepository extends JpaRepository<Operacao, Long> {
    List<Operacao> findByClienteId(Long idCliente);
    List<Operacao> findByContaIdOrderByDataSolicitacaoAsc(Long contaId);
}
