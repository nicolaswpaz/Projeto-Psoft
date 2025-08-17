package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoResponseDTO;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Ativos")
public class AtivoControllerTests {

    final String URI_ATIVOS = "/ativos";

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
    @DisplayName("Conjunto de casos de verificação de nome ao alterar Ativo")
    class AtivoAlteracaoNome {

        @Test
        @DisplayName("Quando alteramos o nome do ativo com dados válidos (exige Admin)")
        void quandoAlteramosNomeDoAtivoValido() throws Exception {

            // Arrange
            ativoPostPutRequestDTO.setNome("Ativo de teste alterado");

            //Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador como um parâmetro de requisição
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals("Ativo de teste alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do ativo para nulo (exige Admin)")
        void quandoAlteramosNomeDoAtivoNulo() throws Exception {

            // Arrange
            ativoPostPutRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Nome obrigatorio", resultado.getErrors().get(0));

        }

        @Test
        @DisplayName("Quando alteramos o nome do ativo para vazio (exige Admin)")
        void quandoAlteramosNomeDoAtivoVazio() throws Exception {
            // Arrange
            ativoPostPutRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class AtivoVerificacaoFluxosBasicosApiRest {

        @Test
        @DisplayName("Quando buscamos por todos ativos salvos")
        void quandoBuscamosPorTodosAtivosSalvos() throws Exception {
            // Arrange
            // Já temos 1 ativo do setup(), vamos adicionar mais 2
            Ativo ativo2 = Ativo.builder()
                    .nome("Ativo Secundario")
                    .tipo(TipoAtivo.ACAO)
                    .disponivel(true)
                    .descricao("Descrição do ativo secundário")
                    .cotacao("20.00")
                    .build();
            Ativo ativo3 = Ativo.builder()
                    .nome("Outro Ativo")
                    .tipo(TipoAtivo.CRIPTOMOEDA)
                    .disponivel(false)
                    .descricao("Descrição de outro ativo")
                    .cotacao("30000.00")
                    .build();
            ativoRepository.saveAll(Arrays.asList(ativo2, ativo3));

            // Act
            String responseJsonString = driver.perform(get(URI_ATIVOS))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertEquals(3, resultado.size());
        }

        @Test
        @DisplayName("Quando buscamos um ativo salvo pelo id")
        void quandoBuscamosPorUmAtivoSalvo() throws Exception {
            // Act
            String responseJsonString = driver.perform(get(URI_ATIVOS + "/" + ativo.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            // Assert
            assertEquals(ativo.getNome(), resultado.getNome());

        }

        @Test
        @DisplayName("Quando buscamos um ativo inexistente")
        void quandoBuscamosPorUmAtivoInexistente() throws Exception {
            // Act
            String responseJsonString = driver.perform(get(URI_ATIVOS + "/" + 999999999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando criamos um novo ativo com dados válidos")
        void quandoCriarAtivoValido() throws Exception {
            // Arrange
            AtivoPostPutRequestDTO novoAtivoDTO = AtivoPostPutRequestDTO.builder()
                    .nome("Novo ativo valido")
                    //.tipoAtivo(TipoAtivo.ACAO)
                    .disponivel(false)
                    .descricao("Descricao do novo ativo")
                    .cotacao("75.50")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_ATIVOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador, pois criar exige admin
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(novoAtivoDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            // Assert
            assertEquals(novoAtivoDTO.getNome(), resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o ativo com dados válidos")
        void quandoAlteramosAtivoValido() throws Exception {
            // Arrange
            Long ativoId = ativo.getId();
            // Altera o nome no DTO que será enviado
            ativoPostPutRequestDTO.setNome("Ativo Principal Alterado");
            ativoPostPutRequestDTO.setCotacao("55.50");

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador, pois alterar exige admin
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(ativoId, resultado.getId()),
                    () -> assertEquals("Ativo Principal Alterado", resultado.getNome()),
                    () -> assertEquals("55.50", resultado.getCotacao())
            );
        }

        @Test
        @DisplayName("Quando alteramos o ativo inexistente")
        void quandoAlteramosAtivoInexistente() throws Exception {
            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos o ativo com matrícula do admin inválida")
        void quandoAlteramosAtivoComMatriculaInvalida() throws Exception {
            // Arrange
            Long ativoId = ativo.getId();
            ativoPostPutRequestDTO.setNome("Ativo admin inválido");

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", "matricula_invalida")
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Autenticacao falhou!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando excluímos um ativo salvo")
        void quandoExcluimosAtivoValido() throws Exception {
            // Act
            String responseJsonString = driver.perform(delete(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador, pois remover exige admin
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
            assertFalse(ativoRepository.existsById(ativo.getId()));
        }


        @Test
        @DisplayName("Quando excluímos um ativo inexistente")
        void quandoExcluimosAtivoInexistente() throws Exception {
            // Act
            String responseJsonString = driver.perform(delete(URI_ATIVOS + "/" + 999999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos autenticar um administrador com matrícula inválida")
        void quandoAutenticamosAdminComMatriculaInvalida() throws Exception {
            // Arrange
            Long ativoId = ativo.getId();
            String matriculaInvalida = "matricula_fake";
            ativoPostPutRequestDTO.setNome("Alteração com matrícula inválida");

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaInvalida)
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Autenticacao falhou!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando buscamos um ativo salvo pelo id, seu tipo deve ser retornado corretamente")
        void quandoBuscamosUmAtivoSalvoVerificamosOSeuTipo() throws Exception {
            // Act
            String responseJsonString = driver.perform(get(URI_ATIVOS + "/" + ativo.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(ativo.getId(), resultado.getId()),
                    () -> assertEquals("ACAO", resultado.getTipo().name()));
        }
    }
    @Nested
    @DisplayName("Conjunto de casos de verificação da atualização de disponibilidade do Ativo")
    class AtivoAtualizacaoDisponibilidade {

        @Test
        @DisplayName("Quando tentamos a disponibilidade do ativo, ativando ou desativando")
        void quandoAlteramosADisponibilidadeDoAtivo() throws Exception {
            Long ativoId = ativo.getId();
            String matriculaValida = administrador.getMatricula();

            String responseDisponibilizarAtivo = driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaValida))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String responseIndisponibilizarAtivo = driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaValida))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertNotNull(responseDisponibilizarAtivo);
            assertNotNull(responseIndisponibilizarAtivo);
        }

        @Test
        @DisplayName("Quando tentamos alterar a disponibilidade de um ativo inexistente")
        void quandoAlteramosADisponibilidadeDeUmAtivoInexistente() throws Exception {
            Long idInvalido = 99992999L;

            String responseDisponibilizarAtivo = driver.perform(put(URI_ATIVOS + "/" + idInvalido + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))

                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String responseIndisponibilizarAtivo = driver.perform(put(URI_ATIVOS + "/" + idInvalido + "/indisponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado1 = objectMapper.readValue(responseIndisponibilizarAtivo, CustomErrorType.class);
            CustomErrorType resultado2 = objectMapper.readValue(responseDisponibilizarAtivo, CustomErrorType.class);

            assertEquals("O ativo consultado nao existe!", resultado1.getMessage());
            assertEquals("O ativo consultado nao existe!", resultado2.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos alterar a disponibilidade de um ativo com administrador inválido")
        void quandoAlteramosADisponibilidadeDeUmAtivoComAdminInvalido() throws Exception {
            Long ativoId = ativo.getId();
            String matriculaInvalida = "matricula_fake";

            String responseDisponibilizarAtivo = driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaInvalida))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            String responseIndisponibilizarAtivo = driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaInvalida))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado1 = objectMapper.readValue(responseDisponibilizarAtivo, CustomErrorType.class);
            CustomErrorType resultado2 = objectMapper.readValue(responseIndisponibilizarAtivo, CustomErrorType.class);

            assertEquals("Autenticacao falhou!", resultado1.getMessage());
            assertEquals("Autenticacao falhou!", resultado2.getMessage());
        }

        @Test
        @DisplayName("Quando disponibilizamos um ativo, seu estado deve mudar para disponível")
        void quandoDisponibilizamosAtivoEstadoDeveSerDisponivel() throws Exception {
            Long ativoId = ativo.getId();
            String matriculaValida = administrador.getMatricula();

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .param("matriculaAdmin", matriculaValida))
                    .andExpect(status().isOk());

            Ativo atualizado = ativoRepository.findById(ativoId).orElseThrow();
            assertTrue(atualizado.isDisponivel());
        }

        @Test
        @DisplayName("Quando indisponibilizamos um ativo, seu estado deve mudar para indisponível")
        void quandoIndisponibilizamosAtivoEstadoDeveSerIndisponivel() throws Exception {
            Long ativoId = ativo.getId();
            String matriculaValida = administrador.getMatricula();

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .param("matriculaAdmin", matriculaValida))
                    .andExpect(status().isOk());

            Ativo atualizado = ativoRepository.findById(ativoId).orElseThrow();
            assertFalse(atualizado.isDisponivel());
        }

        @Test
        @DisplayName("Quando disponibilizamos o ativo duas vezes seguidas não deve falhar")
        void quandoDisponibilizamosDuasVezesNaoDeveFalhar() throws Exception {
            Long ativoId = ativo.getId();
            String matricula = administrador.getMatricula();

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isOk());

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Quando indisponibilizamos o ativo duas vezes seguidas não deve falhar")
        void quandoIndisponibilizamosDuasVezesNaoDeveFalhar() throws Exception {
            Long ativoId = ativo.getId();
            String matricula = administrador.getMatricula();

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isOk());

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da atualização de cotação")
    class AtivoAtualizacaoCotacao{

        @Test
        @DisplayName("Quando atualizamos a cotacao de um ativo com dados validos")
        void quandoAtualizamosCotacaoAtivoValido() throws Exception{

            // Arrange
            //10% de aumento
            double novaCotacao = 1.10;

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", String.valueOf(novaCotacao)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(ativo.getId(), resultado.getId()),
                    () -> assertEquals(String.valueOf(novaCotacao), resultado.getCotacao())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar a cotação de um ativo do tipo TESOURO_DIRETO (não permitido)")
        void quandoTentamosAtualizarCotacaoDeTesouroDireto() throws Exception {
            // Arrange
            Ativo tesouroAtivo = ativoRepository.save(Ativo.builder()
                    .nome("Tesouro Selic 2029")
                    .tipo(TipoAtivo.TESOURO_DIRETO)
                    .disponivel(true)
                    .descricao("Título do Tesouro Nacional")
                    .cotacao("130.50")
                    .build());
            double novaCotacao = 135.00;

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + tesouroAtivo.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", String.valueOf(novaCotacao)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Somente ativos do tipo Acao ou Criptomoeda podem ter a cotacao atualizada", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos atualizar a cotação com uma variação menor que 1% (não permitido)")
        void quandoTentamosAtualizarCotacaoComVariacaoMenorQueUmPorcento() throws Exception {
            // Arrange
            // O ativo do setup tem cotação "1.00". Uma variação para "1.005" é de 0.5%, menor que o 1% exigido.
            double novaCotacaoPequenaVariacao = 1.005;

            // Act
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", String.valueOf(novaCotacaoPequenaVariacao)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("A variacao da cotacao deve ser de no minimo 1%", resultado.getMessage());
        }
    }
}