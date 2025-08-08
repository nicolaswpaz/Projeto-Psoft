package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/clientes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarCliente(
            @PathVariable Long id,
            @RequestParam String codigo) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperar(id, codigo));
    }

    @GetMapping("")
    public ResponseEntity<?> listarClientes(
            @RequestParam String matriculaAdmin) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listar(matriculaAdmin));
    }

    @PostMapping()
    public ResponseEntity<?> criarCliente(
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(clientePostPutRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.alterar(id, codigo, clientePostPutRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirCliente(
            @PathVariable Long id,
            @RequestParam String codigo) {
        clienteService.remover(id, codigo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{id}/ativosDisponiveis")
    public ResponseEntity<?> listarAtivosDisponiveisPorPlano(@PathVariable Long id, @RequestParam String codigo) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listarAtivosDisponiveisPorPlano(id, codigo));
    }

    @PutMapping("/{id}/interesseAtivoIndisponivel")
    public ResponseEntity<?> marcarInteresseEmAtivoIndisponivel(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestParam Long idAtivo) {

        clienteService.marcarInteresseAtivoIndisponivel(id, codigo, idAtivo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @PutMapping("/{id}/interesseAtivoDisponivel")
    public ResponseEntity<?> marcarInteresseEmAtivoDisponivel(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestParam Long idAtivo) {

        clienteService.marcarInteresseAtivoDisponivel(id, codigo, idAtivo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{idCliente}/ativos/{idAtivo}")
    public ResponseEntity<?> detalharAtivoParaCompra(
            @PathVariable Long idCliente,
            @PathVariable Long idAtivo,
            @RequestParam String codigoAcesso
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.visualizarDetalhesAtivo(idCliente, codigoAcesso, idAtivo));
    }

}