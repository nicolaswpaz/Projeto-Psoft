package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.Administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.exception.Ativo.AtivoDisponivelException;
import com.ufcg.psoft.commerce.exception.Ativo.AtivoIndisponivelException;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.conta.ContaService;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.service.conta.notificacao.NotificacaoAtivoVariouCotacao;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

    final String URI_CLIENTES = "/clientes";
    final String URI_ATIVOS = "/ativos";

    @Autowired
    MockMvc driver;

    @Autowired
    AtivoRepository ativoRepository;
    Ativo ativo1;
    Ativo ativo2;
    Ativo ativo3;
    Ativo ativoIndisponivel;
    Ativo ativoDisponivel;
    @Autowired
    ClienteRepository clienteRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;

    Cliente clientePremium;
    ClientePostPutRequestDTO clientePostPutRequestDTO;

    @Autowired
    EnderecoRepository enderecoRepository;

    Endereco endereco;
    EnderecoResponseDTO enderecoDTO;

    @Autowired
    ClienteService clienteService;

    AdministradorPostPutRequestDTO administradorPostPutRequestDTO;
    Administrador administrador;

    @Autowired
    AdministradorRepository administradorRepository;

    @Autowired
    ContaRepository contaRepository;
    Conta contaCliente;
    Conta contaClientePremium;
    @Autowired
    ContaService contaService;

    @Autowired
    EntityManager entityManager;
    ListAppender<ILoggingEvent> listAppender;
    ListAppender<ILoggingEvent> listAppenderAtivoDisponivel;
    @BeforeEach
    @Transactional
    void setup() {

        Logger logger = (Logger) LoggerFactory.getLogger(NotificacaoAtivoVariouCotacao.class);
        Logger loggerNotificacaoAtivoDisponivel = (Logger) LoggerFactory.getLogger(NotificacaoAtivoDisponivel.class);
        listAppender = new ListAppender<>();
        listAppenderAtivoDisponivel = new ListAppender<>();
        listAppender.start();
        listAppenderAtivoDisponivel.start();
        logger.addAppender(listAppender);
        loggerNotificacaoAtivoDisponivel.addAppender(listAppenderAtivoDisponivel);

        objectMapper.registerModule(new JavaTimeModule());

        endereco = /*enderecoRepository.save(*/Endereco.builder()
                .rua("Rua dos testes")
                .bairro("Bairro testado")
                .numero("123")
                .complemento("")
                .cep("58400-000")
                .build();
        contaClientePremium = contaRepository.save(Conta.builder()
                .saldo("500.00")
                .build()

        );
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .cpf("12345678910")
                .codigo("123456")
                .build()
        );

        clientePremium = clienteRepository.save(Cliente.builder()
                        .nome("Cliente Premium da Silva")
                        .endereco(endereco)
                        .cpf("01987654321")
                        .codigo("123456")
                        .plano(TipoPlano.PREMIUM)
                        .conta(contaClientePremium)
                        .build()
        );

        contaCliente = contaRepository.save(Conta.builder()
                .saldo("10000.00")
                .build()
        );


        enderecoDTO = EnderecoResponseDTO.builder()
                .numero(endereco.getNumero())
                .bairro(endereco.getBairro())
                .cep(endereco.getCep())
                .complemento(endereco.getComplemento())
                .rua(endereco.getRua())
                .build();
        clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                .nome(cliente.getNome())
                .enderecoDTO(enderecoDTO)
                .codigo(cliente.getCodigo())
                .cpf(cliente.getCpf())
                .build();

        ativo1 = Ativo.builder()
                .nome("Ativo1")
                .tipo(TipoAtivo.TESOURO_DIRETO)
                .disponivel(true)
                .descricao("Descrição do ativo1")
                .cotacao("20.00")
                .build();
        ativo2 = Ativo.builder()
                .nome("Ativo2")
                .tipo(TipoAtivo.ACAO)
                .disponivel(true)
                .descricao("Descrição do ativo2")
                .cotacao("20.00")
                .build();
        ativo3 = Ativo.builder()
                .nome("Ativo3")
                .tipo(TipoAtivo.TESOURO_DIRETO)
                .disponivel(false)
                .descricao("Descrição do ativo3")
                .cotacao("30000.00")
                .build();
        ativoIndisponivel = ativoRepository.save(Ativo.builder()
                .nome("Ação Rara S.A.")
                .cotacao("250.00")
                .tipo(TipoAtivo.ACAO)
                .descricao("Ativo indisponivel")
                .disponivel(false)
                .build()
        );
        ativoDisponivel = ativoRepository.save(Ativo.builder()
                .nome("AtivoDisponivel")
                .cotacao("100.00")
                .tipo(TipoAtivo.ACAO)
                .descricao("Ativo disponivel")
                .disponivel(true)
                .build());
        ativoRepository.saveAll(Arrays.asList(ativo1, ativo2, ativo3));
    }

    @AfterEach
    void tearDown() {

        ativoRepository.deleteAll();
        clienteRepository.deleteAll();
        contaRepository.deleteAll();
        listAppender.stop();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class ClienteVerificacaoNome {

        @Test
        @DisplayName("Quando recuperamos um cliente com dados válidos")
        void quandoRecuperamosNomeDoClienteValido() throws Exception {

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                    .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertEquals("Cliente Um da Silva", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente com dados válidos")
        void quandoAlteramosNomeDoClienteValido() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome("Cliente Um Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertEquals("Cliente Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente nulo")
        void quandoAlteramosNomeDoClienteNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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

        @Test
        @DisplayName("Quando alteramos o nome do cliente vazio")
        void quandoAlteramosNomeDoClienteVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
    @DisplayName("Conjunto de casos de verificação do endereço")
    class ClienteVerificacaoEndereco {

        @Test
        @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
        void quandoAlteramosEnderecoDoClienteValido() throws Exception {
            // Arrange
            EnderecoResponseDTO novoEndereco = EnderecoResponseDTO.builder()
                    .rua("Nova Rua")
                    .bairro("Novo Bairro")
                    .numero("123")  // Alterado para string numérica
                    .cep("12345-678")  // Formato padrão de CEP
                    .complemento("Novo Complemento")
                    .build();

            // Garante que o cliente tem um endereço existente (se necessário)
            if (cliente.getEndereco() == null) {
                cliente.setEndereco(new Endereco());
                cliente = clienteRepository.save(cliente);
            }

            clientePostPutRequestDTO.setEnderecoDTO(novoEndereco);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())  // Mantido para debug
                    .andReturn().getResponse().getContentAsString();

            // Assert
            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertAll(
                    () -> assertEquals(novoEndereco.getRua(), resultado.getEndereco().getRua()),
                    () -> assertEquals(novoEndereco.getBairro(), resultado.getEndereco().getBairro()),
                    () -> assertEquals(novoEndereco.getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(novoEndereco.getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(novoEndereco.getComplemento(), resultado.getEndereco().getComplemento())
            );
        }


        @Test
        @DisplayName("Quando alteramos o endereço do cliente nulo")
        void quandoAlteramosEnderecoDoClienteNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setEnderecoDTO(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o endereço do cliente vazio")
        void quandoAlteramosEnderecoDoClienteVazio() throws Exception {
            // Arrange
            EnderecoResponseDTO enderecoVazio = EnderecoResponseDTO.builder()
                    .rua("")
                    .bairro(null)
                    .numero("")
                    .cep(null)
                    .build();

            clientePostPutRequestDTO.setEnderecoDTO(enderecoVazio);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue( resultado.getErrors().contains("Rua obrigatoria")),
                    () -> assertTrue( resultado.getErrors().contains("Bairro obrigatorio")),
                    () -> assertTrue( resultado.getErrors().contains("Numero obrigatorio")),
                    () -> assertTrue( resultado.getErrors().contains("CEP obrigatorio"))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class ClienteVerificacaoCodigoAcesso {

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente nulo")
        void quandoAlteramosCodigoAcessoDoClienteNulo() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigo(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente vazio")
        void quandoAlteramosCodigoAcessoDoClienteVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigo("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue( resultado.getErrors().contains("Codigo de acesso obrigatorio")),
                    () -> assertTrue( resultado.getErrors().contains("Codigo de acesso deve ter exatamente 6 digitos numericos"))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente mais de 6 digitos")
        void quandoAlteramosCodigoAcessoDoClienteMaisDe6Digitos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigo("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente menos de 6 digitos")
        void quandoAlteramosCodigoAcessoDoClienteMenosDe6Digitos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigo("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente caracteres não numéricos")
        void quandoAlteramosCodigoAcessoDoClienteCaracteresNaoNumericos() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigo("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o codigo de cliente para um novo válido")
        void quandoAlteramosCodigoDoClienteValido() throws Exception {
            // Arrange
            clientePostPutRequestDTO.setCodigo("987654");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            Cliente clienteAtualizado = clienteRepository.findById(cliente.getId()).orElseThrow();

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals("987654", clienteService.autenticar(cliente.getId(), "987654").getCodigo())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class ClienteVerificacaoFluxosBasicosApiRest {

        @Test
        @Transactional
        @DisplayName("Quando buscamos por todos clientes salvos")
        void quandoBuscamosPorTodosClienteSalvos() throws Exception {
            // Arrange
            clienteRepository.deleteAll();

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


            // Cria endereços primeiro
            Endereco endereco1 = enderecoRepository.save(Endereco.builder()
                    .rua("Av. da Pits A")
                    .numero("100")
                    .bairro("Centro")
                    .cep("58400-000")
                    .complemento("")
                    .build());

            Endereco endereco2 = enderecoRepository.save(Endereco.builder()
                    .rua("Distrito dos Testadores")
                    .numero("200")
                    .bairro("Zona Rural")
                    .cep("58400-123")
                    .complemento("Fazenda")
                    .build());

            Endereco endereco3 = enderecoRepository.save(Endereco.builder()
                    .rua("Rua dos Devs")
                    .numero("300")
                    .bairro("Tecnopolis")
                    .cep("58400-456")
                    .complemento("Sala 42")
                    .build());

            // Cria e salva 3 clientes
            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Dois Almeida")
                    .endereco(endereco1)
                    .codigo("246810")
                    .cpf("11122233344")
                    .build());

            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Tres Lima")
                    .endereco(endereco2)
                    .codigo("135790")
                    .cpf("22233344455")
                    .build());

            Cliente cliente3 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Quatro Silva")
                    .endereco(endereco3)
                    .codigo("987654")
                    .cpf("33344455566")
                    .build());

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES)
                            .param("matriculaAdmin", "admin123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<ClienteResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

            System.out.println("Resultado recebido da API:");
            resultado.forEach(cliente -> System.out.println(cliente));


            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size(), "Deveriam retornar 3 clientes"),
                    () -> assertTrue(resultado.stream().anyMatch(c -> c.getNome().equals("Cliente Dois Almeida"))),
                    () -> assertTrue(resultado.stream().anyMatch(c -> c.getNome().equals("Cliente Tres Lima"))),
                    () -> assertTrue(resultado.stream().anyMatch(c -> c.getNome().equals("Cliente Quatro Silva"))),
                    () -> assertEquals("Av. da Pits A", resultado.get(0).getEndereco().getRua()),
                    () -> assertEquals("Distrito dos Testadores", resultado.get(1).getEndereco().getRua()),
                    () -> assertEquals("Rua dos Devs", resultado.get(2).getEndereco().getRua())
            );
        }

        @Test
        @DisplayName("Quando buscamos um cliente salvo pelo id")
        void quandoBuscamosPorUmClienteSalvo() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                            .param("codigo", cliente.getCodigo())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(cliente.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando buscamos um cliente inexistente")
        void quandoBuscamosPorUmClienteInexistente() throws Exception {
            // Arrange
            Long idInexistente = 99999999L;
            String codigoQualquer = "123456"; // Código qualquer, já que o cliente não existe

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", codigoQualquer)) // Adiciona o parâmetro codigo
                    .andExpect(status().isBadRequest()) // Código 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com dados válidos")
        void quandoCriarClienteValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com nome null")
        void quandoCriarClienteNomeNull() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome(null)
                    .enderecoDTO(enderecoDTO)
                    .codigo("654321")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com nome vazio")
        void quandoCriarClienteNomeVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("")  // Nome vazio deve ser rejeitado
                    .enderecoDTO(enderecoDTO)
                    .codigo("654321")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Espera 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().contains("Nome obrigatorio"))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com endereço null")
        void quandoCriarClienteEnderecoNull() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Sem Edereço")
                    .enderecoDTO(null)
                    .codigo("654321")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com endereço sem dados")
        void quandoCriarClienteEndereçoVazio() throws Exception {
            // Arrange
            enderecoDTO = EnderecoResponseDTO.builder()
                    .numero("")
                    .bairro("")
                    .cep("")
                    .complemento("")
                    .rua("")
                    .build();
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Sem Edereço")
                    .enderecoDTO(enderecoDTO)
                    .codigo("654321")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().contains("Rua obrigatoria")),
                    () -> assertTrue(resultado.getErrors().contains("Bairro obrigatorio")),
                    () -> assertTrue(resultado.getErrors().contains("Numero obrigatorio")),
                    () -> assertTrue(resultado.getErrors().contains("CEP obrigatorio"))
            );
        }


        @Test
        @DisplayName("Quando criamos um novo cliente com plano premium")
        void quandoCriarClientePlanoPremium() throws Exception {
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Premium")
                    .enderecoDTO(enderecoDTO)
                    .codigo("654321")
                    .cpf("12345678910")
                    .plano(TipoPlano.PREMIUM)
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals(clientePostPutRequestDTO.getPlano(), resultado.getPlano())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com plano null por padrão normal")
        void quandoCriarClientePlanoNull() throws Exception {
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Premium")
                    .enderecoDTO(enderecoDTO)
                    .codigo("654321")
                    .cpf("12345678910")
                    .plano(null)
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals("NORMAL", resultado.getPlano().name())
            );
        }

        @Test
        @DisplayName("Quando tentamos criar um novo cliente com código null - deve retornar erro")
        void quandoCriarClienteCodigoNull() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente sem Código")
                    .enderecoDTO(enderecoDTO)
                    .codigo(null)  // Código null deve ser rejeitado
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Espera 400, não 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com codigo vazio")
        void quandoCriarClienteCodigoVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente sem Codigo")
                    .enderecoDTO(enderecoDTO)
                    .codigo("")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com codigo maior que 6 digitos")
        void quandoCriarClienteCodigoInvalidoMaior() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Codigo Invalido")
                    .enderecoDTO(enderecoDTO)
                    .codigo("1234567")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com codigo menor que 6 digitos")
        void quandoCriarClienteCodigoInvalidoMenor() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Codigo Invalido")
                    .enderecoDTO(enderecoDTO)
                    .codigo("12345")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com codigo não numerico")
        void quandoCriarClienteCodigoInvalido() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Codigo Invalido")
                    .enderecoDTO(enderecoDTO)
                    .codigo("batata")
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com cpf null")
        void quandoCriarClienteCpfNull() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Sem cpf")
                    .enderecoDTO(enderecoDTO)
                    .codigo("123456")
                    .cpf(null)
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com cpf vazio")
        void quandoCriarClienteCpfVazio() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Sem cpf")
                    .enderecoDTO(enderecoDTO)
                    .codigo("123456")
                    .cpf("")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com cpf invalido")
        void quandoCriarClienteCpfInvalido() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente cpf Invalido")
                    .enderecoDTO(enderecoDTO)
                    .codigo("123456")
                    .cpf("123")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente com dados válidos")
        void quandoAlteramosClienteValido() throws Exception {
            // Arrange
            Long clienteId = cliente.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), clienteId),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente inexistente")
        void quandoAlteramosClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente passando código de acesso inválido")
        void quandoAlteramosClienteCodigoAcessoInvalido() throws Exception {
            // Arrange
            Long clienteId = cliente.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + clienteId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", "invalido")
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }


        @Test
        @DisplayName("Quando excluímos um cliente inexistente")
        void quandoExcluimosClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
        void quandoExcluimosClienteCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", "invalido"))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }
    }

    @Test
    @DisplayName("Quando listamos ativos disponiveis passando o id de um cliente válido com plano Normal")
    void quandoListamosAtivosClientePlanoNormal() throws Exception {
        //Arrange
        // Plano do cliente geral já é Normal

        // Act
        String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId() + "/ativos-disponiveis")
                        .param("codigo", cliente.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});
        List<String> nomesEsperados = List.of("Ativo1");
        List<String> nomesRetornados = resultado.stream().map(AtivoResponseDTO::getNome).toList();


        // Assert
        assertTrue(nomesRetornados.containsAll(nomesEsperados));
        assertEquals(1, nomesRetornados.size());
        assertFalse(nomesRetornados.isEmpty());
    }

    @Test
    @DisplayName("Quando listamos ativos disponiveis passando o id de um cliente válido com plano Premium")
    void quandoListamosAtivosClientePlanoPremium() throws Exception {
        //Arrange
        cliente.setPlano(TipoPlano.PREMIUM);
        clienteRepository.save(cliente);

        // Act
        String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId() + "/ativos-disponiveis")
                        .param("codigo", cliente.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});
        List<String> nomesEsperados = List.of("Ativo1", "Ativo2");
        List<String> nomesRetornados = resultado.stream().map(AtivoResponseDTO::getNome).toList();


        // Assert
        assertTrue(nomesRetornados.containsAll(nomesEsperados));
        assertEquals(2, nomesRetornados.size());
        assertFalse(nomesRetornados.isEmpty());
    }

    @Test
    @DisplayName("Quando tentamos listar ativos passando um cliente inexistente")
    void quandoListamosAtivosClienteInexistente() throws Exception {
        // Arrange
        // nenhuma necessidade além do setup()

        // Act
        String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + 999999999 + "/ativos-disponiveis")
                        .param("codigo", "123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // Codigo 400
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        // Assert
        assertAll(
                () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
        );
    }

    @Test
    @DisplayName("Quando tentamos listar ativos de um cliente, mas não existe ativos diponiveis")
    void quandoTentamosListarAtivosDisponiveisQuandoNaoExistem() throws Exception {
        //Arrange
        ativo1.setDisponivel(false);
        ativo2.setDisponivel(false);
        ativoRepository.saveAll(List.of(ativo1, ativo2));

        cliente.setPlano(TipoPlano.PREMIUM);
        clienteRepository.save(cliente);

        // Act
        String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId() + "/ativos-disponiveis")
                        .param("codigo", cliente.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Quando tentamos listar ativos de um cliente passando um código de acesso incorreto")
    void quandoTentamosListarAtivosCodigoClienteInvalido() throws Exception {
        // Arrange
        // nenhuma necessidade além do setup()

        // Act
        String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId() + "/ativos-disponiveis")
                        .param("codigo", "000000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        // Assert
        assertAll(
                () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
        );
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos de Mensagem e Notificação do Cliente")
    class ClienteNotificacaoMensagem {

        @Test
        @DisplayName("Cliente Premium registra interesse em ativo disponível com sucesso")
        void testeclienteRegistraInteresseAtivoDisponivel() {
            // set o cliente como Premium
            cliente.setPlano(TipoPlano.PREMIUM);
            cliente.setConta(contaCliente);
            clienteRepository.save(cliente);
            // Act

            //Usamos o Ativo 2 como sendo do tipo ACAO
            clienteService.marcarInteresseAtivoDisponivel(cliente.getId(), cliente.getCodigo(), ativo2.getId());

            // Assert
            Conta contaAtualizada = contaRepository.findById(cliente.getConta().getId()).orElseThrow();
            entityManager.flush(); //faz ele executar todos os inserts
            entityManager.refresh(contaAtualizada);// Atualiza o cash do hibernete
            assertTrue(contaAtualizada.getAtivosDeInteresse().stream().anyMatch(a -> a.getId().equals(ativo2.getId())));
        }

        @Test
        @DisplayName("Notificar cliente Premium quando cotação de ativo de interesse varia mais de 10%")
        void testeNotificarclienteQuandoCotacaoVariaMaisDe10PorCento() {
            // Arrange
            cliente.setPlano(TipoPlano.PREMIUM);
            cliente.setConta(contaCliente);
            clienteRepository.save(cliente);

            clienteService.marcarInteresseAtivoDisponivel(cliente.getId(), cliente.getCodigo(), ativo2.getId());

            // Simula a variação de cotação superior a 10%
            ativo2.setCotacao("23.00"); // Variação > 10%
            Ativo ativoAtualizado = ativoRepository.save(ativo2);

            // Act

            contaService.notificarClientesPremiumComInteresse(ativoAtualizado);

            // Assert
            List<ILoggingEvent> logsList = listAppender.list;
            assertEquals(1, logsList.size());
            String logMessage = logsList.get(0).getFormattedMessage();

            assertTrue(logMessage.contains("Caro cliente " + cliente.getNome() + ", o ativo que você marcou interesse, teve uma taxa de variação de cotação acima de 10%!"));
            assertTrue(logMessage.contains("Nome do ativo: " + ativoAtualizado.getNome()));
            assertTrue(logMessage.contains("O tipo do ativo: " + ativoAtualizado.getTipo().toString()));
            assertTrue(logMessage.contains("Cotação do ativo: " + ativoAtualizado.getCotacao()));
            assertTrue(logMessage.contains("Descrição do ativo: " + ativoAtualizado.getDescricao()));
        }

        @Test
        @DisplayName("Notificar cliente Premium quando cotação de ativo de interesse varia mais de 10%, MAS ELE NÃO MARCOU INTERESSE")
        void testeNotificarclienteQuandoCotacaoVariaMaisDe10PorCentoMasClienteNaoMarcouInteresse() {
            // Arrange
            cliente.setPlano(TipoPlano.PREMIUM);
            cliente.setConta(contaCliente);
            clienteRepository.save(cliente);

            ativo2.setCotacao("23.00"); // Variação > 10%
            Ativo ativoAtualizado = ativoRepository.save(ativo2);

            // Act

            contaService.notificarClientesPremiumComInteresse(ativoAtualizado);

            // Assert
            List<ILoggingEvent> logsList = listAppender.list;
            assertEquals(0, logsList.size());

        }

        @Test
        @DisplayName("Cliente Normal não pode registrar interesse em ativo disponível (deve lançar exceção)")
        void testeClienteNormalNaoPodeRegistrarInteresseEmAtivoDisponivel() {
            // Arrange
            cliente.setPlano(TipoPlano.NORMAL);
            cliente.setConta(contaCliente);
            clienteRepository.save(cliente);
            // Act & AssertClienteNaoPremiumException()
            assertThrows(ClienteNaoPremiumException.class, () -> {
                clienteService.marcarInteresseAtivoDisponivel(cliente.getId(), cliente.getCodigo(), ativo2.getId());
            });
        }

        @Test
        @DisplayName("Cliente Premium não pode registrar interesse em ativo indisponível (deve lançar exceção)")
        void testeClientePremiumNaoPodeRegistrarInteresseEmAtivoIndisponivel() {
            cliente.setPlano(TipoPlano.PREMIUM);
            cliente.setConta(contaCliente);
            clienteRepository.save(cliente);

            ativo2.setDisponivel(false);
            // Act & Assert
            assertThrows(AtivoIndisponivelException.class, () -> {
                clienteService.marcarInteresseAtivoDisponivel(cliente.getId(), cliente.getCodigo(), ativo2.getId());
            });
        }

        @Test
        @DisplayName("Cliente (NORMAL) com interesse deve ser notificado quando ativo fica disponível")
        void testeClienteNormalComInteresseQuandoAtivoFicaDisponivelEntaoDeveSerNotificado() {

            cliente.setPlano(TipoPlano.NORMAL);
            cliente.setConta(contaCliente);
            clienteRepository.save(cliente);

            clienteService.marcarInteresseAtivoIndisponivel(cliente.getId(), cliente.getCodigo(), ativoIndisponivel.getId());

            // O ativo se torna disponível
            ativoIndisponivel.setDisponivel(true);
            Ativo ativoAgoraDisponivel = ativoRepository.save(ativoIndisponivel);

            // Act
            contaService.notificarAtivoDisponivelClientesComInteresse(ativoAgoraDisponivel);

            // Assert
            List<ILoggingEvent> logsList = listAppenderAtivoDisponivel.list;
            assertEquals(1, logsList.size());
            String logMessage = logsList.get(0).getFormattedMessage();

            assertTrue(logMessage.contains("Caro cliente " + cliente.getNome() + ", o ativo indisponível que você marcou interesse está disponível!"));
            assertTrue(logMessage.contains("Dados do Ativo:"));
            assertTrue(logMessage.contains("Nome do ativo: " + ativoAgoraDisponivel.getNome()));
            assertTrue(logMessage.contains("O tipo do ativo: " + ativoAgoraDisponivel.getTipo().toString()));
            assertTrue(logMessage.contains("Cotação do ativo: " + ativoAgoraDisponivel.getCotacao().toString()));
            assertTrue(logMessage.contains("Descrição do ativo: " + ativoAgoraDisponivel.getDescricao()));
        }

        @Test
        @DisplayName("Cliente (PREMIUM) com interesse deve ser notificado quando ativo fica disponível")
        void testeClientePremiumComInteresseQuandoAtivoFicaDisponivelEntaoDeveSerNotificado() {

            clienteService.marcarInteresseAtivoIndisponivel(clientePremium.getId(), clientePremium.getCodigo(), ativoIndisponivel.getId());

            // O ativo se torna disponível
            ativoIndisponivel.setDisponivel(true);
            Ativo ativoAgoraDisponivel = ativoRepository.save(ativoIndisponivel);

            // Act
            contaService.notificarAtivoDisponivelClientesComInteresse(ativoAgoraDisponivel);

            // Assert
            List<ILoggingEvent> logsList = listAppenderAtivoDisponivel.list;
            assertEquals(1, logsList.size());
            String logMessage = logsList.get(0).getFormattedMessage();

            assertTrue(logMessage.contains("Caro cliente " + clientePremium.getNome() + ", o ativo indisponível que você marcou interesse está disponível!"));
            assertTrue(logMessage.contains("Dados do Ativo:"));
            assertTrue(logMessage.contains("Nome do ativo: " + ativoAgoraDisponivel.getNome()));
            assertTrue(logMessage.contains("O tipo do ativo: " + ativoAgoraDisponivel.getTipo().toString()));
            assertTrue(logMessage.contains("Cotação do ativo: " + ativoAgoraDisponivel.getCotacao().toString()));
            assertTrue(logMessage.contains("Descrição do ativo: " + ativoAgoraDisponivel.getDescricao()));
        }

        @Test
        @DisplayName("Nenhuma notificação deve ser enviada se ninguém tiver interesse no ativo")
        void testeNenhumaNotificacaoSeNinguemTiverInteresse() {
            // Arrange

            ativoIndisponivel.setDisponivel(true);
            Ativo ativoAgoraDisponivel = ativoRepository.save(ativoIndisponivel);

            // Act
            contaService.notificarAtivoDisponivelClientesComInteresse(ativoAgoraDisponivel);


            assertTrue(listAppenderAtivoDisponivel.list.isEmpty(), "Nenhuma notificação deveria ter sido gerada.");
        }

        @Test
        @DisplayName("Deve falhar ao tentar registrar interesse em ativo que já está disponível")
        void testeFalhaAoMarcarInteresseEmAtivoJaDisponivel() {
            // Arrange
            // Usa-se o 'ativoJaDisponivel' criado no setUp

            // Act & Assert
            assertThrows(AtivoDisponivelException.class, () -> {
                clienteService.marcarInteresseAtivoIndisponivel(clientePremium.getId(), clientePremium.getCodigo(), ativoDisponivel.getId());
            }, "Deveria lançar AtivoDisponivelException ao marcar interesse em ativo já disponível.");
        }
    }
}
