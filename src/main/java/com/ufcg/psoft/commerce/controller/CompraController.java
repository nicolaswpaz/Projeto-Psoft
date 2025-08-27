package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.service.operacao.compra.CompraService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/compras",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class CompraController {

    private final CompraService compraService;
    protected final ModelMapper modelMapper;

    public CompraController(CompraService compraService, ModelMapper modelMapper) {
        this.compraService = compraService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/{idCliente}/{idAtivo}")
    public ResponseEntity<CompraResponseDTO> solicitarCompra(
            @PathVariable Long idCliente,
            @PathVariable Long idAtivo,
            @RequestParam String codigoAcesso,
            @RequestParam int quantidade) {

        CompraResponseDTO compraDTO = compraService.solicitarCompra(idCliente, codigoAcesso, idAtivo, quantidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraDTO);
    }

    @PutMapping("/admin/{idCompra}/disponibilizar")
    public ResponseEntity<CompraResponseDTO> disponibilizarCompra(
            @PathVariable Long idCompra,
            @RequestParam String matriculaAdmin) {

        CompraResponseDTO compraDTO = compraService.disponibilizarCompra(idCompra, matriculaAdmin);
        return ResponseEntity.ok(compraDTO);
    }

    @PutMapping("/{idCliente}/{idCompra}/confirmar")
    public ResponseEntity<CompraResponseDTO> confirmarCompra(
            @PathVariable Long idCliente,
            @PathVariable Long idCompra,
            @RequestParam String codigoAcesso) {

        CompraResponseDTO compraDTO = compraService.confirmarCompra(idCliente, codigoAcesso, idCompra);
        return ResponseEntity.ok(compraDTO);
    }

    @GetMapping("/{idCliente}/{idCompra}")
    public ResponseEntity<CompraResponseDTO> consultarCompra(
            @PathVariable Long idCliente,
            @PathVariable Long idCompra,
            @RequestParam String codigoAcesso) {

        CompraResponseDTO compraDTO = compraService.consultar(idCliente, codigoAcesso, idCompra);
        return ResponseEntity.ok(compraDTO);
    }

    @GetMapping("/admin/{matriculaAdmin}")
    public ResponseEntity<List<CompraResponseDTO>> listarCompras(@PathVariable String matriculaAdmin) {
        List<CompraResponseDTO> compras = compraService.listar(matriculaAdmin);
        return ResponseEntity.ok(compras);
    }
}
