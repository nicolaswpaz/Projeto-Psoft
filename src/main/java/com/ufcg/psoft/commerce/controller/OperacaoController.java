package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.service.extrato.ExtratoService;
import com.ufcg.psoft.commerce.service.operacao.OperacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/operacoes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OperacaoController {

    private final OperacaoService operacaoService;
    private final ExtratoService extratoService;

    @GetMapping("/{idCliente}")
    public ResponseEntity<List<OperacaoResponseDTO>> consultarOperacaoCliente(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso,
            @RequestParam(required = false) String tipoAtivo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String statusOperacao) {

        List<OperacaoResponseDTO> operacaoDTO = operacaoService.consultarOperacaoCliente(idCliente, codigoAcesso, tipoAtivo, dataInicio, dataFim, statusOperacao);
        return ResponseEntity.ok(operacaoDTO);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<OperacaoResponseDTO>> consultarResgateCompraAdmin(
            @RequestParam String matriculaAdmin,
            @RequestParam(required = false) Long idCliente,
            @RequestParam(required = false) String tipoAtivo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(required = false) String tipoOperacao) {

        List<OperacaoResponseDTO> operacaoDTO = operacaoService.consultarOperacoesComAdmin(matriculaAdmin, idCliente, tipoAtivo, data, tipoOperacao);
        return ResponseEntity.ok(operacaoDTO);
    }

    @GetMapping("/clientes/{idCliente}/gerarExtrato")
    public ResponseEntity<StreamingResponseBody> exportarExtratoCSV(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso) {

        StreamingResponseBody stream = outputStream -> extratoService.gerarExtratoCSV(idCliente, codigoAcesso, outputStream);

        String currentDateTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = String.format("extrato_cliente_%d_%s.csv", idCliente, currentDateTime);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(stream);
    }
}