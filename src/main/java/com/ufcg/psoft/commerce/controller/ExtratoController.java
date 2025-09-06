package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.service.extrato.ExtratoServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping(
        value = "/extratos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ExtratoController {

    private final ExtratoServiceImpl extratoServiceImpl;

    public ExtratoController(ExtratoServiceImpl extratoServiceImpl) {
        this.extratoServiceImpl = extratoServiceImpl;
    }

    @GetMapping(
            value = "/cliente/{idCliente}/csv",
            produces = "text/csv"
    )
    public ResponseEntity<StreamingResponseBody> exportarExtratoCSV(
            @PathVariable Long idCliente,
            @RequestParam String codigoAcesso) {

        StreamingResponseBody stream = outputStream -> extratoServiceImpl.gerarExtratoCSV(idCliente, codigoAcesso, outputStream);

        String currentDateTime = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = String.format("extrato_cliente_%d_%s.csv", idCliente, currentDateTime);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(stream);
    }
}