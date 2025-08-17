package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findTopBy();
}
