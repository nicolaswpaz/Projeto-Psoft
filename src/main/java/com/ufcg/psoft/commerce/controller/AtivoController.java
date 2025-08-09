package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
public class AtivoController {

    @Autowired
    AtivoService ativoService;

    @PostMapping()
    public ResponseEntity<AtivoResponseDTO> criarAtivo(@RequestParam String matriculaAdmin,
                                                       @RequestBody @Valid AtivoPostPutRequestDTO ativoPostPutRequestDTO){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ativoService.criar(matriculaAdmin, ativoPostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtivoResponseDTO> atualizarAtivo(@RequestParam String matriculaAdmin,
                                            @PathVariable Long id,
                                            @RequestBody @Valid AtivoPostPutRequestDTO ativoPostPutRequestDTO){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.alterar(matriculaAdmin, id, ativoPostPutRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtivoResponseDTO> recuperarAtivo(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.recuperarDetalhado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAtivo(@RequestParam String matriculaAdmin, @PathVariable Long id){
        ativoService.remover(matriculaAdmin, id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}/disponibilizar")
    public ResponseEntity<AtivoResponseDTO> tornarAtivoDisponivel(
            @RequestParam String matriculaAdmin,
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.tornarDisponivel(matriculaAdmin, id));
    }

    @PutMapping("/{id}/indisponibilizar")
    public ResponseEntity<AtivoResponseDTO> tornarAtivoIndisponivel(
            @RequestParam String matriculaAdmin,
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.tornarIndisponivel(matriculaAdmin, id));
    }

    @PutMapping("/{id}/cotacao")
    public ResponseEntity<AtivoResponseDTO> atualizarCotacao(@PathVariable Long id,
                                              @RequestParam String matriculaAdmin,
                                              @RequestParam BigDecimal novoValor) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.atualizarCotacao(matriculaAdmin, id, novoValor));
    }

    @GetMapping
    public ResponseEntity<List<AtivoResponseDTO>> listarAtivos(
            @RequestParam(required = false, defaultValue = "") String nome
    ) {
        if (!nome.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ativoService.listarPorNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.listar());
    }
}
