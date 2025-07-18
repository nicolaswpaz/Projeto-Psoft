package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
public class AtivoController {

    @Autowired
    AtivoService ativoService;

    @PostMapping()
    public ResponseEntity<?> criarAtivo(@RequestParam String matriculaAdmin,
                                        @RequestBody @Valid AtivoPostPutRequestDTO ativoPostPutRequestDTO){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ativoService.criar(matriculaAdmin, ativoPostPutRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAtivo(@RequestParam String matriculaAdmin,
                                            @PathVariable Long id,
                                            @RequestBody @Valid AtivoPostPutRequestDTO ativoPostPutRequestDTO){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.alterar(matriculaAdmin, id, ativoPostPutRequestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarAtivo(@PathVariable Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.recuperar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirAtivo(@RequestParam String matriculaAdmin, @PathVariable Long id){
        ativoService.remover(matriculaAdmin, id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    public ResponseEntity<?> listarAtivos(@RequestParam(required = false, defaultValue = "") String nome){
        if (nome != null && !nome.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ativoService.listarPorNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.listar());
    }
}
