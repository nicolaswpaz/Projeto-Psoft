package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.service.extrato.ExtratoServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.text.SimpleDateFormat;
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
            value = "/cliente/{clienteId}/csv",
            produces = "text/csv"
    )
    public ResponseEntity<StreamingResponseBody> exportarExtratoCSV(
            @PathVariable Long clienteId,
            @RequestParam String codigoAcesso) {

        StreamingResponseBody stream = outputStream -> {
            extratoServiceImpl.gerarExtratoCSV(clienteId, codigoAcesso, outputStream);
        };

        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String filename = String.format("extrato_cliente_%d_%s.csv", clienteId, currentDateTime);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(stream);
    }
}