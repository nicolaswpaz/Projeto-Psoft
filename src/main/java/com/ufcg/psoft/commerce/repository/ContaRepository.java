package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

}
