package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public class AdministradorController {
    @Autowired
    private AdministradorService administradorService;

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> criarAdministrador(@RequestBody AdministradorPostPutRequestDTO dto) {
        AdministradorResponseDTO response = administradorService.criar(dto);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> atualizarAdministrador(
            @PathVariable Long id,
            @RequestBody AdministradorPostPutRequestDTO dto,
            @RequestParam String matricula
    ) {
        Administrador adminAtualizado = administradorService.atualizarAdmin(dto, matricula);
        return ResponseEntity.ok(adminAtualizado);
    }

    @DeleteMapping
    public ResponseEntity<Void> removerAdministrador(@RequestParam String matricula) {
        administradorService.removerAdmin(matricula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> buscarAdministrador(@PathVariable Long id) {
        Administrador admin = administradorService.getAdmin();
        return ResponseEntity.ok(admin);
    }
}
