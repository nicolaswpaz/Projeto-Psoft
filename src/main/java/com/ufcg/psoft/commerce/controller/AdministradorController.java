package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdministradorController {
    @Autowired
    AdministradorService administradorService;

    @PostMapping
    public ResponseEntity<?> criarAdministrador(@RequestBody AdministradorPostPutRequestDTO dto) {
        AdministradorResponseDTO response = administradorService.criar(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(administradorService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAdministrador(
            @PathVariable Long id,
            @RequestBody AdministradorPostPutRequestDTO dto,
            @RequestParam String matricula
    ) {
        Administrador adminAtualizado = administradorService.atualizarAdmin(dto, matricula);
        return ResponseEntity.ok(adminAtualizado);
    }

    @DeleteMapping
    public ResponseEntity<?> removerAdministrador(@RequestParam String matricula) {
        administradorService.removerAdmin(matricula);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAdministrador(@PathVariable Long id) {
        Administrador admin = administradorService.getAdmin();
        return ResponseEntity.ok(admin);
    }
}
