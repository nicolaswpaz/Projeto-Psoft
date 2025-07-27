package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.model.Administrador;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EnderecoRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
public class ClienteTestAula {

    final String URI_CLIENTES = "/clientes";

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    AdministradorRepository administradorRepository;
    Administrador administrador;
    AdministradorPostPutRequestDTO administradorPostPutRequestDTO;

    ObjectMapper objectMapper = new ObjectMapper();

    List<ClienteResponseDTO> clientesDTO = new ArrayList<>();

    @BeforeEach
    @Transactional
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        administrador = administradorRepository.save(Administrador.builder()
                .matricula("admin123")
                .nome("Admin Teste")
                .cpf("11122233344")
                .endereco(Endereco.builder()
                        .cep("12345678")
                        .bairro("Um lugar aí")
                        .rua("Avenida Qualquer")
                        .numero("15")
                        .build())
                .build()
        );

        administradorPostPutRequestDTO = AdministradorPostPutRequestDTO.builder()
                .matricula(administrador.getMatricula())
                .nome(administrador.getNome())
                .cpf(administrador.getCpf())
                .enderecoDTO(new EnderecoPostPutRequestDTO())
                .build();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de teste da aula")
    class ClienteVerificacaoNome {

        @Test
        @Transactional
        @DisplayName("Quando recuperamos clientes")
        void quandoRecuperamosClientesValidos() throws Exception {
            // Arrange
            String stringBusca = "Cliente";
            String matriculaAdmin = administrador.getMatricula(); // Usa a matrícula do admin criado no setup

            // Persiste os endereços antes de associá-los aos clientes
            Endereco endereco1 = enderecoRepository.save(Endereco.builder()
                    .numero("123")
                    .cep("58400-000")
                    .rua("Rua 123")
                    .complemento("")
                    .bairro("bairro 1")
                    .build());

            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente")
                    .endereco(endereco1)
                    .codigo("123456")
                    .cpf("12345678914")
                    .build());

            // Atualiza a lista de clientes esperada
            List<ClienteResponseDTO> clientesEsperados = List.of(
                    ClienteResponseDTO.builder()
                            .nome(cliente1.getNome())
                            .endereco(new EnderecoResponseDTO(cliente1.getEndereco()))
                            .id(cliente1.getId())
                            .build()
            );

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/busca")
                            .param("nome", stringBusca)
                            .param("matriculaAdmin", matriculaAdmin)) // Usa a matrícula correta
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            String expectedResult = objectMapper.writeValueAsString(clientesEsperados);
            assertEquals(expectedResult, responseJsonString);
        }
    }
}


