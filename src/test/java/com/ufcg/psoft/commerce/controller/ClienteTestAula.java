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
    AdministradorRepository administradorRepository;
    Administrador administrador;
    AdministradorPostPutRequestDTO administradorPostPutRequestDTO;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    List<ClienteResponseDTO> clientesDTO = new ArrayList<>();

    @BeforeEach
    @Transactional
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        //enderecos fictícios para os testes

        Endereco endereco1 = /*enderecoRepository.save(*/Endereco.builder()
                .numero("123")
                .cep("58400-000")
                .rua("Rua 123")
                .complemento("")
                .bairro("bairro 1")
                .build();

        Endereco endereco2 = /*enderecoRepository.save(*/Endereco.builder()
                .bairro("bairro 2")
                .complemento("")
                .numero("234")
                .rua("Rua 234")
                .cep("40028922")
                .build();

        Cliente cliente1 = clienteRepository.save(Cliente.builder()
                .nome("Cliente")
                .endereco(endereco1)
                .codigo("123456")
                .cpf("12345678914")
                .build()
        );

        Cliente cliente2 = clienteRepository.save(Cliente.builder()
                .nome("Clienta")
                .endereco(endereco2)
                .codigo("123456")
                .cpf("12345678910")
                .build()
        );

        ClienteResponseDTO r1 = ClienteResponseDTO.builder()
                .nome(cliente1.getNome())
                .endereco(new EnderecoResponseDTO(cliente1.getEndereco())) //gera o objeto EnderecoResponseDTO a partir do objeto Endereco
                .id(cliente1.getId())
                .build();

        Endereco enderecoAdmin = enderecoRepository.save(
                Endereco.builder()
                        .cep("12345678")
                        .bairro("Um lugar aí")
                        .rua("Avenida Qualquer")
                        .numero("15")
                        .build()
        );

        administrador = administradorRepository.save(Administrador.builder()
                .matricula("admin123")
                .nome("Admin Teste")
                .cpf("11122233344")
                .endereco(enderecoAdmin)  // Agora salvo!
                .build()
        );

        clientesDTO.add(r1);
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de teste da aula")
    class ClienteVerificacaoNome {

        @Test
        @DisplayName("Quando recuperamos clientes")
        void quandoRecuperamosClientesValidos() throws Exception {

            String stringBusca = "Cliente";
            String matriculaAdmin = "admin123";

            //Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/busca")
                            .param("nome", stringBusca)
                            .param("matriculaAdmin", "admin123"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String expectedResult = objectMapper
                    .writeValueAsString(clientesDTO);

            System.out.println(expectedResult);
            System.out.println(responseJsonString);
            // Assert
            assertEquals(expectedResult, responseJsonString);
        }

    }
}


