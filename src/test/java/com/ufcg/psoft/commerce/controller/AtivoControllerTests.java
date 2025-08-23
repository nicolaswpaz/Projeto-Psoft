package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Ativos")
class AtivoControllerTests {

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
                .cotacao(BigDecimal.valueOf(1.00))
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
            ativoPostPutRequestDTO.setNome("Ativo de teste alterado");

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
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

            ativoPostPutRequestDTO.setNome(null);

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Nome obrigatorio", resultado.getErrors().get(0));

        }

        @Test
        @DisplayName("Quando alteramos o nome do ativo para vazio (exige Admin)")
        void quandoAlteramosNomeDoAtivoVazio() throws Exception {

            ativoPostPutRequestDTO.setNome("");

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            // Adiciona a matricula do administrador
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

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

            Ativo ativo2 = Ativo.builder()
                    .nome("Ativo Secundario")
                    .tipo(TipoAtivo.ACAO)
                    .disponivel(true)
                    .descricao("Descrição do ativo secundário")
                    .cotacao(BigDecimal.valueOf(20.00))
                    .build();
            Ativo ativo3 = Ativo.builder()
                    .nome("Outro Ativo")
                    .tipo(TipoAtivo.CRIPTOMOEDA)
                    .disponivel(false)
                    .descricao("Descrição de outro ativo")
                    .cotacao(BigDecimal.valueOf(30000.00))
                    .build();
            ativoRepository.saveAll(Arrays.asList(ativo2, ativo3));

            String responseJsonString = driver.perform(get(URI_ATIVOS))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertEquals(3, resultado.size());
        }

        @Test
        @DisplayName("Quando buscamos um ativo salvo pelo id")
        void quandoBuscamosPorUmAtivoSalvo() throws Exception {

            String responseJsonString = driver.perform(get(URI_ATIVOS + "/" + ativo.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(ativo.getNome(), resultado.getNome());

        }

        @Test
        @DisplayName("Quando buscamos um ativo inexistente")
        void quandoBuscamosPorUmAtivoInexistente() throws Exception {

            String responseJsonString = driver.perform(get(URI_ATIVOS + "/" + 999999999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando criamos um novo ativo com dados válidos")
        void quandoCriarAtivoValido() throws Exception {
            AtivoPostPutRequestDTO novoAtivoDTO = AtivoPostPutRequestDTO.builder()
                    .nome("Novo ativo valido")
                    .disponivel(false)
                    .descricao("Descricao do novo ativo")
                    .cotacao(BigDecimal.valueOf(75.50))
                    .build();

            String responseJsonString = driver.perform(post(URI_ATIVOS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(novoAtivoDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(novoAtivoDTO.getNome(), resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o ativo com dados válidos")
        void quandoAlteramosAtivoValido() throws Exception {

            Long ativoId = ativo.getId();

            ativoPostPutRequestDTO.setNome("Ativo Principal Alterado");
            ativoPostPutRequestDTO.setCotacao(BigDecimal.valueOf(55.50));

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertAll(
                    () -> assertEquals(ativoId, resultado.getId()),
                    () -> assertEquals("Ativo Principal Alterado", resultado.getNome()),
                    () -> assertEquals(BigDecimal.valueOf(55.50), resultado.getCotacao())
            );
        }

        @Test
        @DisplayName("Quando alteramos o ativo inexistente")
        void quandoAlteramosAtivoInexistente() throws Exception {
            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos o ativo com matrícula do admin inválida")
        void quandoAlteramosAtivoComMatriculaInvalida() throws Exception {
            Long ativoId = ativo.getId();
            ativoPostPutRequestDTO.setNome("Ativo admin inválido");

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", "matricula_invalida")
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Autenticacao falhou!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando excluímos um ativo salvo")
        void quandoExcluimosAtivoValido() throws Exception {

            String responseJsonString = driver.perform(delete(URI_ATIVOS + "/" + ativo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
            assertFalse(ativoRepository.existsById(ativo.getId()));
        }


        @Test
        @DisplayName("Quando excluímos um ativo inexistente")
        void quandoExcluimosAtivoInexistente() throws Exception {

            String responseJsonString = driver.perform(delete(URI_ATIVOS + "/" + 999999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos autenticar um administrador com matrícula inválida")
        void quandoAutenticamosAdminComMatriculaInvalida() throws Exception {
            Long ativoId = ativo.getId();
            String matriculaInvalida = "matricula_fake";
            ativoPostPutRequestDTO.setNome("Alteração com matrícula inválida");

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaInvalida)
                            .content(objectMapper.writeValueAsString(ativoPostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Autenticacao falhou!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando buscamos um ativo salvo pelo id, seu tipo deve ser retornado corretamente")
        void quandoBuscamosUmAtivoSalvoVerificamosOSeuTipo() throws Exception {

            String responseJsonString = driver.perform(get(URI_ATIVOS + "/" + ativo.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertAll(
                    () -> assertEquals(ativo.getId(), resultado.getId()),
                    () -> assertEquals(TipoAtivo.ACAO, resultado.getTipo()));
        }
    }
    @Nested
    @DisplayName("Conjunto de casos de verificação da atualização de disponibilidade do Ativo")
    class AtivoAtualizacaoDisponibilidade {

        @Test
        @DisplayName("Quando tentamos a disponibilidade do ativo, ativando ou desativando")
        void quandoAlteramosADisponibilidadeDoAtivo() throws Exception {
            Ativo ativoIndisponivel = ativoRepository.save(Ativo.builder()
                    .nome("Ativo Indisponível")
                    .tipo(TipoAtivo.ACAO)
                    .disponivel(false)
                    .descricao("Descrição do ativo 1")
                    .cotacao(BigDecimal.valueOf(1.00))
                    .build()
            );

            Long ativoId = ativoIndisponivel.getId();
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
            long idInvalido = 99992999L;

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
            Ativo ativoIndisponivel = ativoRepository.save(Ativo.builder()
                    .nome("Ativo Indisponível")
                    .tipo(TipoAtivo.ACAO)
                    .disponivel(false)
                    .descricao("Descrição do ativo 1")
                    .cotacao(BigDecimal.valueOf(1.00))
                    .build()
            );

            Long ativoId = ativoIndisponivel.getId();
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
        @DisplayName("Quando disponibilizamos o ativo duas vezes seguidas deve falhar")
        void quandoDisponibilizamosDuasVezesNaoDeveFalhar() throws Exception {
            Ativo ativoIndisponivel = ativoRepository.save(Ativo.builder()
                    .nome("Ativo Indisponível")
                    .tipo(TipoAtivo.ACAO)
                    .disponivel(false)
                    .descricao("Descrição do ativo 1")
                    .cotacao(BigDecimal.valueOf(1.00))
                    .build()
            );

            Long ativoId = ativoIndisponivel.getId();
            String matricula = administrador.getMatricula();

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isOk());

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/disponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Quando indisponibilizamos o ativo duas vezes seguidas deve falhar")
        void quandoIndisponibilizamosDuasVezesNaoDeveFalhar() throws Exception {
            Long ativoId = ativo.getId();
            String matricula = administrador.getMatricula();

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isOk());

            driver.perform(put(URI_ATIVOS + "/" + ativoId + "/indisponibilizar")
                            .param("matriculaAdmin", matricula))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da atualização de cotação")
    class AtivoAtualizacaoCotacao{

        @Test
        @DisplayName("Quando atualizamos a cotacao de um ativo com dados validos")
        void quandoAtualizamosCotacaoAtivoValido() throws Exception{

            BigDecimal novaCotacao = BigDecimal.valueOf(1.10);


            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", String.valueOf(novaCotacao)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertAll(
                    () -> assertEquals(ativo.getId(), resultado.getId()),
                    () -> assertEquals(novaCotacao, resultado.getCotacao())
            );
        }

        @Test
        @DisplayName("Quando tentamos atualizar a cotação de um ativo do tipo TESOURO_DIRETO (não permitido)")
        void quandoTentamosAtualizarCotacaoDeTesouroDireto() throws Exception {

            Ativo tesouroAtivo = ativoRepository.save(Ativo.builder()
                    .nome("Tesouro Selic 2029")
                    .tipo(TipoAtivo.TESOURO_DIRETO)
                    .disponivel(true)
                    .descricao("Título do Tesouro Nacional")
                    .cotacao(BigDecimal.valueOf(130.50))
                    .build());
            BigDecimal novaCotacao = BigDecimal.valueOf(135.00);

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + tesouroAtivo.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", String.valueOf(novaCotacao)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Somente ativos do tipo Acao ou Criptomoeda podem ter a cotacao atualizada", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentamos atualizar a cotação com uma variação menor que 1% (não permitido)")
        void quandoTentamosAtualizarCotacaoComVariacaoMenorQueUmPorcento() throws Exception {

            BigDecimal novaCotacaoPequenaVariacao = BigDecimal.valueOf(1.005);

            String responseJsonString = driver.perform(put(URI_ATIVOS + "/" + ativo.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", String.valueOf(novaCotacaoPequenaVariacao)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("A variacao da cotacao deve ser de no minimo 1%", resultado.getMessage());
        }
    }
}