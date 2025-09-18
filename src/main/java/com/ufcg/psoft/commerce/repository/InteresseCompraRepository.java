package com.ufcg.psoft.commerce.repository;


import com.ufcg.psoft.commerce.model.Compra;
import com.ufcg.psoft.commerce.model.InteresseCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteresseCompraRepository extends JpaRepository<InteresseCompra, Long> {
    List<InteresseCompra> findByCompra(Compra compra);
}
