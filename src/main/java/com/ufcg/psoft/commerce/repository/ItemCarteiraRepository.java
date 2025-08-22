package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.AtivoEmCarteira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCarteiraRepository extends JpaRepository<AtivoEmCarteira, Long> {
}
