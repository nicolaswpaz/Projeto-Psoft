package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorResponseDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoPostPutRequestDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.repository.AdministradorRepository;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Administrador")
public class AdministradorControllerTests {

    final String URI_ADMINISTRADORES = "/administrador";

    @Autowired
    MockMvc driver;

    @Autowired
    AtivoRepository ativoRepository;
    Ativo ativo;
    AtivoPostPutRequestDTO ativoPostPutRequestDTO;

    @Autowired
    AdministradorRepository administradorRepository;
    Administrador administrador;
    AdministradorPostPutRequestDTO administradorPostPutRequestDTO;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
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

        ativo = ativoRepository.save(Ativo.builder()
                .nome("Ativo 1")
                .tipo(TipoAtivo.ACAO)
                .disponivel(true)
                .descricao("Descrição do ativo 1")
                .cotacao("1.00")
                .build()
        );

        ativoPostPutRequestDTO = AtivoPostPutRequestDTO.builder()
                .nome(ativo.getNome())
                .disponivel(ativo.getDisponivel())
                .descricao(ativo.getDescricao())
                .cotacao(ativo.getCotacao())
                .build();
    }

    @AfterEach
    void tearDown() {
        ativoRepository.deleteAll();
        administradorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do Administrador")
    class AdministradorFluxoTestes {

        @Test
        @DisplayName("Quando buscamos o único administrador existente")
        void quandoBuscamosAdministradorExistente() throws Exception {
            // Act
            String responseJsonString = driver.perform(get(URI_ADMINISTRADORES + "/" + administrador.getMatricula())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(administradorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AdministradorResponseDTO resultado = objectMapper.readValue(responseJsonString, AdministradorResponseDTO.class);

            // Assert
            assertEquals(administrador.getNome(), resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos dados do único administrador")
        void quandoAlteramosAdministrador() throws Exception {
            // Arrange
            administradorPostPutRequestDTO.setNome("Administrador Alterado");

            EnderecoPostPutRequestDTO enderecoAlterado = new EnderecoPostPutRequestDTO();
            enderecoAlterado.setCep("87654321");
            enderecoAlterado.setRua("Avenida Brasil");
            enderecoAlterado.setNumero("1000");
            enderecoAlterado.setBairro("Centro");

            administradorPostPutRequestDTO.setEnderecoDTO(enderecoAlterado);

            // Act
            String responseJsonString = driver.perform(put(URI_ADMINISTRADORES + "/" + administrador.getMatricula())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(administradorPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AdministradorResponseDTO resultado = objectMapper.readValue(responseJsonString, AdministradorResponseDTO.class);

            // Assert
            assertEquals("Administrador Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando tentamos alterar administrador com matrícula inválida")
        void quandoAlteramosAdministradorMatriculaInvalida() throws Exception {
            String matriculaInvalida = "matricula_fake";
            administradorPostPutRequestDTO.setNome("Tentativa de alteração inválida");

            EnderecoPostPutRequestDTO enderecoAlterado = new EnderecoPostPutRequestDTO();
            enderecoAlterado.setCep("87654321");
            enderecoAlterado.setRua("Avenida Brasil");
            enderecoAlterado.setNumero("1000");
            enderecoAlterado.setBairro("Centro");

            administradorPostPutRequestDTO.setEnderecoDTO(enderecoAlterado);

            // Act
            String responseJsonString = driver.perform(put(URI_ADMINISTRADORES + "/" + matriculaInvalida)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(administradorPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Autenticacao falhou!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos criar um segundo administrador")
        void quandoCriamosSegundoAdministrador() throws Exception {
            // Arrange
            AdministradorPostPutRequestDTO novoAdmin = AdministradorPostPutRequestDTO.builder()
                    .matricula("novaMatricula")
                    .nome("Novo Admin")
                    .cpf("99999999999")
                    .enderecoDTO(new EnderecoPostPutRequestDTO())
                    .build();

            // Act
            String responseJsonString = driver.perform(post("/administrador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoAdmin)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Ja existe um administrador cadastrado no sistema.", resultado.getMessage());
        }
    }
}
