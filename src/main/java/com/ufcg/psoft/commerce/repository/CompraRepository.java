package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findAllByContaIdAndStatusCompra(Long contaId, StatusCompra statusCompra);
}
