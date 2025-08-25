package com.ufcg.psoft.commerce.controller;

import  com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(
        value = "/clientes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteController {


    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> recuperarCliente(
            @PathVariable Long id,
            @RequestParam String codigo) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperar(id, codigo));
    }

    @GetMapping("")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientes(
            @RequestParam String matriculaAdmin) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listar(matriculaAdmin));
    }

    @PostMapping()
    public ResponseEntity<ClienteResponseDTO> criarCliente(
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(clientePostPutRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @RequestParam String codigo,
            @RequestBody @Valid ClientePostPutRequestDTO clientePostPutRequestDto) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.alterar(id, codigo, clientePostPutRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(
            @PathVariable Long id,
            @RequestParam String codigo) {

        clienteService.remover(id, codigo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}/ativosDisponiveis")
    public ResponseEntity<List<AtivoResponseDTO>> listarAtivosDisponiveisPorPlano(
            @PathVariable Long id,
            @RequestParam String codigo) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listarAtivosDisponiveisPorPlano(id, codigo));
    }

    @PutMapping("/{id}/interesseAtivoIndisponivel/{idAtivo}")
    public ResponseEntity<Void> marcarInteresseEmAtivoIndisponivel(
            @PathVariable Long id,
            @RequestParam String codigo,
            @PathVariable Long idAtivo) {

        clienteService.marcarInteresseAtivoIndisponivel(id, codigo, idAtivo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{id}/interesseAtivoDisponivel/{idAtivo}")
    public ResponseEntity<Void> marcarInteresseEmAtivoDisponivel(
            @PathVariable Long id,
            @RequestParam String codigo,
            @PathVariable Long idAtivo) {

        clienteService.marcarInteresseAtivoDisponivel(id, codigo, idAtivo);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{id}/detalharAtivo/{idAtivo}")
    public ResponseEntity<AtivoResponseDTO> detalharAtivoParaCompra(
            @PathVariable Long id,
            @PathVariable Long idAtivo,
            @RequestParam String codigo
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.visualizarDetalhesAtivo(id, codigo, idAtivo));
    }

    @GetMapping("/{idCliente}/carteira")
    public ResponseEntity<List<AtivoEmCarteiraResponseDTO>> visualizarCarteira(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso) {

        List<AtivoEmCarteiraResponseDTO> carteira = clienteService.visualizarCarteira(idCliente, codigoAcesso);

        return ResponseEntity.ok(carteira);
    }

    @PutMapping("/{idCliente}/conta/depositar")
    public ResponseEntity<Void> acrecentaSaldoConta(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso,
            @RequestParam BigDecimal valor) {
        clienteService.acrecentaSaldoConta(idCliente, codigoAcesso, valor);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}