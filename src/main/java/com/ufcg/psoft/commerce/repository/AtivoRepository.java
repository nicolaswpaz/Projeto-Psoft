package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AtivoRepository extends JpaRepository<Ativo, Long> {

    List<Ativo> findByNomeContaining(String nome);

    List<Ativo> findByDisponivelTrue();

}
