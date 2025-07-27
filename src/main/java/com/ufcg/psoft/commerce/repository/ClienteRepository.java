package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("SELECT c FROM Cliente c WHERE c.nome LIKE %:nome%")
    List<Cliente> buscarPorNome(@Param("nome") String nome);

    Long id(Long id);
}
