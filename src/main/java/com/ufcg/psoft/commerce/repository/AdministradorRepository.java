package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByMatricula(String matricula);
}
