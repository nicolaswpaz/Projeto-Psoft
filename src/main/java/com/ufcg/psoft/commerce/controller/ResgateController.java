package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;
import com.ufcg.psoft.commerce.service.resgate.ResgateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/resgates",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ResgateController {

    private final ResgateService resgateService;

    public ResgateController(ResgateService resgateService) {
        this.resgateService = resgateService;
    }

    @PostMapping("/{idCliente}/{idAtivo}")
    public ResponseEntity<ResgateResponseDTO> solicitarResgate(
            @PathVariable Long idCliente,
            @PathVariable Long idAtivo,
            @RequestParam String codigoAcesso,
            @RequestParam int quantidade) {

        ResgateResponseDTO resgateDTO = resgateService.solicitarResgate(idCliente, codigoAcesso, idAtivo, quantidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(resgateDTO);
    }

    @GetMapping("/{idCliente}/{idResgate}")
    public ResponseEntity<ResgateResponseDTO> consultarResgate(
            @PathVariable Long idCliente,
            @PathVariable Long idResgate,
            @RequestParam String codigoAcesso) {

        ResgateResponseDTO resgateDTO = resgateService.consultar(idCliente, codigoAcesso, idResgate);
        return ResponseEntity.ok(resgateDTO);
    }

    @PutMapping("/admin/{idResgate}/confirmar")
    public ResponseEntity<ResgateResponseDTO> confirmarResgate(
            @PathVariable Long idResgate,
            @RequestParam String matriculaAdmin) {

        ResgateResponseDTO resgateDTO = resgateService.confirmarResgate(idResgate, matriculaAdmin);
        return ResponseEntity.ok(resgateDTO);
    }
}
