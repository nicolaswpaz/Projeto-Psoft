package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findAllByCliente_IdAndStatusCompra(Long clienteId, StatusCompra statusCompra);
}