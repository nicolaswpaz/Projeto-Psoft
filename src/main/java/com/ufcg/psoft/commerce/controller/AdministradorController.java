package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/administrador")
public class AdministradorController {
    @Autowired
    AdministradorService administradorService;

    @PostMapping
    public ResponseEntity<AdministradorResponseDTO> criarAdministrador(
            @RequestBody AdministradorPostPutRequestDTO administradorPostPutRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(administradorService.criar(administradorPostPutRequestDTO));
    }

    @PutMapping("/{matricula}")
    public ResponseEntity<AdministradorResponseDTO> atualizarAdministrador(
            @PathVariable String matricula,
            @RequestBody @Valid AdministradorPostPutRequestDTO administradorPostPutRequestDTO
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(administradorService.atualizarAdmin(administradorPostPutRequestDTO, matricula));
    }

    @DeleteMapping("/{matricula}")
    public ResponseEntity<Void> removerAdministrador(
            @PathVariable String matricula) {
        administradorService.removerAdmin(matricula);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<AdministradorResponseDTO> buscarAdministrador() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(administradorService.buscarAdmin());
    }
}
