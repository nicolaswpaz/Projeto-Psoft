package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.service.operacao.OperacaoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(
        value = "/operacoes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class OperacaoController {

    private final OperacaoService operacaoService;

    public OperacaoController(OperacaoService operacaoService){
        this.operacaoService = operacaoService;
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<List<OperacaoResponseDTO>> consultarOperacaoCliente(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso,
            @RequestParam String tipoAtivo,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam String statusOperacao) {

        List<OperacaoResponseDTO> operacaoDTO = operacaoService.consultarOperacaoCliente(idCliente, codigoAcesso, tipoAtivo, dataInicio, dataFim, statusOperacao);
        return ResponseEntity.ok(operacaoDTO);
    }

    @GetMapping("/admin/")
    public ResponseEntity<List<OperacaoResponseDTO>> consultarResgateCompraAdmin(
            @RequestParam String matriculaAdmin,
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) String tipoAtivo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) String tipoOperacao) {

        List<OperacaoResponseDTO> operacaoDTO = operacaoService.consultarOperacoesComAdmin(matriculaAdmin, idCliente, tipoAtivo, data, tipoOperacao);
        return ResponseEntity.ok(operacaoDTO);
    }
}