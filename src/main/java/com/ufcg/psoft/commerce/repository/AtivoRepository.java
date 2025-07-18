package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtivoRepository extends JpaRepository<Ativo, Long> {

    List<Ativo> findByNomeContaining(String nome);
}
