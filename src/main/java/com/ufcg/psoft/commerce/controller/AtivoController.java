package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ativos", produces = MediaType.APPLICATION_JSON_VALUE)
public class AtivoController {

    @Autowired
    AtivoService ativoService;

    public ResponseEntity<?> recuperarAtivo(){
    }
}
