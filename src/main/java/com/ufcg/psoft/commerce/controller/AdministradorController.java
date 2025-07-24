package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdministradorController {
    @Autowired
    AdministradorService administradorService;

    @PostMapping
    public ResponseEntity<?> criarAdministrador(
            @RequestBody AdministradorPostPutRequestDTO administradorPostPutRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(administradorService.criar(administradorPostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAdministrador(
            @PathVariable Long id,
            @RequestParam String matricula,
            @RequestBody @Valid AdministradorPostPutRequestDTO administradorPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(administradorService.atualizarAdmin(administradorPostPutRequestDTO, matricula));
    }

    @DeleteMapping
    public ResponseEntity<?> removerAdministrador(
            @RequestParam String matricula) {
        administradorService.removerAdmin(matricula);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAdministrador(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(administradorService.getAdmin());
    }
}
