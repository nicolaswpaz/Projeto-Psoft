package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraDTO;
import com.ufcg.psoft.commerce.service.conta.ContaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/contas",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping("/{idCliente}/carteira")
    public ResponseEntity<List<AtivoEmCarteiraDTO>> visualizarCarteira(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso) {

        List<AtivoEmCarteiraDTO> carteira = contaService.visualizarCarteira(idCliente, codigoAcesso);

        return ResponseEntity.ok(carteira);
    }
}