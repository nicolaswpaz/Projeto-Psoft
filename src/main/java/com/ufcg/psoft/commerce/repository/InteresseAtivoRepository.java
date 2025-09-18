package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.InteresseAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface InteresseAtivoRepository extends JpaRepository<InteresseAtivo, Long> {
    List<InteresseAtivo> findByAtivoAndTipoInteresse(Ativo ativo, TipoInteresse tipoInteresse);
}
