package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.administrador.AdministradorPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.autenticacao.AutenticacaoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.listener.NotificacaoAtivoDisponivel;
import com.ufcg.psoft.commerce.listener.NotificacaoAtivoVariouCotacao;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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
class    ClienteControllerTests {

    final String uriClientes = "/clientes";
    final String uriAtivos = "/ativos";

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

    @Autowired
    InteresseAtivoRepository interesseAtivoRepository;

    Endereco endereco;
    EnderecoResponseDTO enderecoDTO;
    Endereco endereco2;
    @Autowired
    ClienteService clienteService;

    @Autowired
    AutenticacaoService autenticacaoService;

    AdministradorPostPutRequestDTO administradorPostPutRequestDTO;
    Administrador administrador;

    @Autowired
    AdministradorRepository administradorRepository;

    @Autowired
    ContaRepository contaRepository;
    Conta contaCliente;
    Conta contaClientePremium;


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

        endereco = enderecoRepository.save(Endereco.builder()
                .rua("Rua dos testes")
                .bairro("Bairro testado")
                .numero("123")
                .complemento("")
                .cep("58400-000")
                .build());
        endereco2 = enderecoRepository.save(Endereco.builder()
                .rua("Rua dos testes2")
                .bairro("Bairro testado")
                .numero("123")
                .complemento("")
                .cep("58400-000")
                .build());
        contaClientePremium = contaRepository.save(Conta.builder()
                .saldo(BigDecimal.valueOf(500.00))
                .build()

        );
        contaCliente = contaRepository.save(Conta.builder()
                .saldo(BigDecimal.valueOf(10000.00))
                .build()
        );
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .plano(TipoPlano.NORMAL)
                .cpf("12345678910")
                .codigo("123456")
                .conta(contaCliente)
                .build()
        );

        clientePremium = clienteRepository.save(Cliente.builder()
                .nome("Cliente Premium da Silva")
                .endereco(endereco2)
                .cpf("01987654321")
                .codigo("123456")
                .plano(TipoPlano.PREMIUM)
                .conta(contaClientePremium)
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
        administrador = administradorRepository.save(Administrador.builder()
                .matricula("admin1234")
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
        ativo1 = Ativo.builder()
                .nome("Ativo1")
                .tipo(TipoAtivo.TESOURO_DIRETO)
                .disponivel(true)
                .descricao("Descrição do ativo1")
                .cotacao(BigDecimal.valueOf(20.00))
                .build();
        ativo2 = Ativo.builder()
                .nome("Ativo2")
                .tipo(TipoAtivo.ACAO)
                .disponivel(true)
                .descricao("Descrição do ativo2")
                .cotacao(BigDecimal.valueOf(20.00))
                .build();
        ativo3 = Ativo.builder()
                .nome("Ativo3")
                .tipo(TipoAtivo.TESOURO_DIRETO)
                .disponivel(false)
                .descricao("Descrição do ativo3")
                .cotacao(BigDecimal.valueOf(30000.00))
                .build();
        ativoIndisponivel = ativoRepository.save(Ativo.builder()
                .nome("Ação Rara S.A.")
                .cotacao(BigDecimal.valueOf(250.00))
                .tipo(TipoAtivo.ACAO)
                .descricao("Ativo indisponivel")
                .disponivel(false)
                .build()
        );
        ativoDisponivel = ativoRepository.save(Ativo.builder()
                .nome("AtivoDisponivel")
                .cotacao(BigDecimal.valueOf(100.00))
                .tipo(TipoAtivo.ACAO)
                .descricao("Ativo disponivel")
                .disponivel(true)
                .build());
        ativoRepository.saveAll(Arrays.asList(ativo1, ativo2, ativo3));
    }

    @AfterEach
    void tearDown() {

        interesseAtivoRepository.deleteAll();
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

            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId())
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertEquals("Cliente Um da Silva", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente com dados válidos")
        void quandoAlteramosNomeDoClienteValido() throws Exception {

            clientePostPutRequestDTO.setNome("Cliente Um Alterado");

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertEquals("Cliente Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente nulo")
        void quandoAlteramosNomeDoClienteNulo() throws Exception {

            clientePostPutRequestDTO.setNome(null);

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente vazio")
        void quandoAlteramosNomeDoClienteVazio() throws Exception {
            clientePostPutRequestDTO.setNome("");

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
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
    @DisplayName("Conjunto de casos de verificação do endereço")
    class ClienteVerificacaoEndereco {

        @Test
        @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
        void quandoAlteramosEnderecoDoClienteValido() throws Exception {

            EnderecoResponseDTO novoEndereco = EnderecoResponseDTO.builder()
                    .rua("Nova Rua")
                    .bairro("Novo Bairro")
                    .numero("123")
                    .cep("12345-678")
                    .complemento("Novo Complemento")
                    .build();

            if (cliente.getEndereco() == null) {
                cliente.setEndereco(new Endereco());
                cliente = clienteRepository.save(cliente);
            }

            clientePostPutRequestDTO.setEnderecoDTO(novoEndereco);

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

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

            clientePostPutRequestDTO.setEnderecoDTO(null);


            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o endereço do cliente vazio")
        void quandoAlteramosEnderecoDoClienteVazio() throws Exception {

            EnderecoResponseDTO enderecoVazio = EnderecoResponseDTO.builder()
                    .rua("")
                    .bairro(null)
                    .numero("")
                    .cep(null)
                    .build();

            clientePostPutRequestDTO.setEnderecoDTO(enderecoVazio);

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

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

            clientePostPutRequestDTO.setCodigo(null);


            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente vazio")
        void quandoAlteramosCodigoAcessoDoClienteVazio() throws Exception {

            clientePostPutRequestDTO.setCodigo("");


            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue( resultado.getErrors().contains("Codigo de acesso obrigatorio")),
                    () -> assertTrue( resultado.getErrors().contains("Codigo de acesso deve ter exatamente 6 digitos numericos"))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente mais de 6 digitos")
        void quandoAlteramosCodigoAcessoDoClienteMaisDe6Digitos() throws Exception {

            clientePostPutRequestDTO.setCodigo("1234567");

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente menos de 6 digitos")
        void quandoAlteramosCodigoAcessoDoClienteMenosDe6Digitos() throws Exception {

            clientePostPutRequestDTO.setCodigo("12345");

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do cliente caracteres não numéricos")
        void quandoAlteramosCodigoAcessoDoClienteCaracteresNaoNumericos() throws Exception {

            clientePostPutRequestDTO.setCodigo("a*c4e@");

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o codigo de cliente para um novo válido")
        void quandoAlteramosCodigoDoClienteValido() throws Exception {

            clientePostPutRequestDTO.setCodigo("987654");

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);


            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals("987654", autenticacaoService.autenticarCliente(cliente.getId(), "987654").getCodigo())
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

            clienteRepository.deleteAll();
            contaRepository.deleteAll();

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

            // 🔹 Salva clientes com endereços diretamente (sem variáveis intermediárias)
            clienteRepository.save(Cliente.builder()
                    .nome("Cliente Dois Almeida")
                    .endereco(enderecoRepository.save(Endereco.builder()
                            .rua("Av. da Pits A")
                            .numero("100")
                            .bairro("Centro")
                            .cep("58400-000")
                            .complemento("")
                            .build()))
                    .codigo("246810")
                    .cpf("11122233344")
                    .build());

            clienteRepository.save(Cliente.builder()
                    .nome("Cliente Tres Lima")
                    .endereco(enderecoRepository.save(Endereco.builder()
                            .rua("Distrito dos Testadores")
                            .numero("200")
                            .bairro("Zona Rural")
                            .cep("58400-123")
                            .complemento("Fazenda")
                            .build()))
                    .codigo("135790")
                    .cpf("22233344455")
                    .build());

            clienteRepository.save(Cliente.builder()
                    .nome("Cliente Quatro Silva")
                    .endereco(enderecoRepository.save(Endereco.builder()
                            .rua("Rua dos Devs")
                            .numero("300")
                            .bairro("Tecnopolis")
                            .cep("58400-456")
                            .complemento("Sala 42")
                            .build()))
                    .codigo("987654")
                    .cpf("33344455566")
                    .build());

            String responseJsonString = driver.perform(get(uriClientes)
                            .param("matriculaAdmin", "admin123")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<ClienteResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

            System.out.println("Resultado recebido da API:");
            resultado.forEach(System.out::println);

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

            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId())
                            .param("codigo", cliente.getCodigo())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(cliente.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando buscamos um cliente inexistente")
        void quandoBuscamosPorUmClienteInexistente() throws Exception {
            Long idInexistente = 99999999L;
            String codigoQualquer = "123456";

            String responseJsonString = driver.perform(get(uriClientes + "/" + idInexistente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", codigoQualquer))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com dados válidos")
        void quandoCriarClienteValido() throws Exception {

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
            );
        }

        @ParameterizedTest(name = "{index} => Campo: {0}, Valor: {1}")
        @MethodSource("fornecerDadosInvalidosCliente")
        void quandoCriarClienteComDadosInvalidos(String campo, String valorInvalido) throws Exception {
            ClientePostPutRequestDTO dto = ClientePostPutRequestDTO.builder()
                    .nome(campo.equals("nome") ? valorInvalido : "Cliente Valido")
                    .enderecoDTO(enderecoDTO)
                    .codigo(campo.equals("codigo") ? valorInvalido : "123456")
                    .cpf(campo.equals("cpf") ? valorInvalido : "12345678910")
                    .build();

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
            );
        }

        private static Stream<Arguments> fornecerDadosInvalidosCliente() {
            return Stream.of(
                    Arguments.of("nome", null),
                    Arguments.of("codigo", ""),
                    Arguments.of("codigo", "1234567"),
                    Arguments.of("codigo", "12345"),
                    Arguments.of("codigo", "batata"),
                    Arguments.of("cpf", null),
                    Arguments.of("cpf", ""),
                    Arguments.of("cpf", "123")
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com nome vazio")
        void quandoCriarClienteNomeVazio() throws Exception {

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("")
                    .enderecoDTO(enderecoDTO)
                    .codigo("654321")
                    .cpf("12345678910")
                    .build();

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertTrue(resultado.getErrors().contains("Nome obrigatorio"))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com endereço null")
        void quandoCriarClienteEnderecoNull() throws Exception {

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente Sem Edereço")
                    .enderecoDTO(null)
                    .codigo("654321")
                    .cpf("12345678910")
                    .build();

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando criamos um novo cliente com endereço sem dados")
        void quandoCriarClienteEnderecoVazio() throws Exception {

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

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

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

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

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

            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome()),
                    () -> assertEquals("NORMAL", resultado.getPlano().name())
            );
        }

        @Test
        @DisplayName("Quando tentamos criar um novo cliente com código null - deve retornar erro")
        void quandoCriarClienteCodigoNull() throws Exception {

            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente sem Código")
                    .enderecoDTO(enderecoDTO)
                    .codigo(null)
                    .cpf("12345678910")
                    .build();


            String responseJsonString = driver.perform(post(uriClientes)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente com dados válidos")
        void quandoAlteramosClienteValido() throws Exception {

            Long clienteId = cliente.getId();

            String responseJsonString = driver.perform(put(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), clienteId),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente inexistente")
        void quandoAlteramosClienteInexistente() throws Exception {

            String responseJsonString = driver.perform(put(uriClientes + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando alteramos o cliente passando código de acesso inválido")
        void quandoAlteramosClienteCodigoAcessoInvalido() throws Exception {

            Long clienteId = cliente.getId();


            String responseJsonString = driver.perform(put(uriClientes + "/" + clienteId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", "invalido")
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }


        @Test
        @DisplayName("Quando excluímos um cliente inexistente")
        void quandoExcluimosClienteInexistente() throws Exception {

            String responseJsonString = driver.perform(delete(uriClientes + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
        void quandoExcluimosClienteCodigoAcessoInvalido() throws Exception {

            String responseJsonString = driver.perform(delete(uriClientes + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", "invalido"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }
    }

    @Test
    @DisplayName("Quando listamos ativos disponiveis passando o id de um cliente válido com plano Normal")
    void quandoListamosAtivosClientePlanoNormal() throws Exception {

        String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/ativosDisponiveis")
                        .param("codigo", cliente.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});
        List<String> nomesEsperados = List.of("Ativo1");
        List<String> nomesRetornados = resultado.stream().map(AtivoResponseDTO::getNome).toList();

        assertTrue(nomesRetornados.containsAll(nomesEsperados));
        assertEquals(1, nomesRetornados.size());
        assertFalse(nomesRetornados.isEmpty());
    }

    @Test
    @DisplayName("Quando listamos ativos disponiveis passando o id de um cliente válido com plano Premium")
    void quandoListamosAtivosClientePlanoPremium() throws Exception {

        cliente.setPlano(TipoPlano.PREMIUM);
        clienteRepository.save(cliente);


        String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/ativosDisponiveis")
                        .param("codigo", cliente.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});
        List<String> nomesEsperados = List.of("Ativo1", "Ativo2", "AtivoDisponivel");
        List<String> nomesRetornados = resultado.stream().map(AtivoResponseDTO::getNome).toList();

        assertTrue(nomesRetornados.containsAll(nomesEsperados));
        assertEquals(3, nomesRetornados.size());
        assertFalse(nomesRetornados.isEmpty());
    }

    @Test
    @DisplayName("Quando tentamos listar ativos passando um cliente inexistente")
    void quandoListamosAtivosClienteInexistente() throws Exception {

        String responseJsonString = driver.perform(get(uriClientes + "/" + 999999999 + "/ativosDisponiveis")
                        .param("codigo", "123456789")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        assertAll(
                () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
        );
    }

    @Test
    @DisplayName("Quando tentamos listar ativos de um cliente, mas não existe ativos diponiveis")
    void quandoTentamosListarAtivosDisponiveisQuandoNaoExistem() throws Exception {

        ativo1.setDisponivel(false);
        ativo2.setDisponivel(false);
        ativoDisponivel.setDisponivel(false);
        ativoRepository.saveAll(List.of(ativo1, ativo2));

        cliente.setPlano(TipoPlano.PREMIUM);
        clienteRepository.save(cliente);


        String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/ativosDisponiveis")
                        .param("codigo", cliente.getCodigo())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        List<AtivoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {});

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Quando tentamos listar ativos de um cliente passando um código de acesso incorreto")
    void quandoTentamosListarAtivosCodigoClienteInvalido() throws Exception {

        String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/ativosDisponiveis")
                        .param("codigo", "000000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();

        CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

        assertAll(
                () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
        );
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos de Mensagem e Notificação do Cliente")
    class ClienteNotificacaoMensagem {

        @Test
        @DisplayName("Quando cliente Premium registra interesse em ativo disponível com sucesso")
        void quandoClienteRegistraInteresseAtivoDisponivel() throws Exception{

            driver.perform(put(uriClientes + "/" + clientePremium.getId() + "/interesseAtivoDisponivel/" + ativoDisponivel.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", clientePremium.getCodigo()))
                    .andExpect(status().isNoContent());

            entityManager.flush();
            entityManager.refresh(ativoDisponivel);

            boolean interesseRegistrado = ativoDisponivel.getInteresses().stream()
                    .anyMatch(interesse -> interesse.getCliente().getId().equals(clientePremium.getId()));

            assertTrue(interesseRegistrado);
        }

        @Test
        @DisplayName("quando Notificamos o cliente Premium quando cotação de ativo de interesse variar mais de 10%")
        void quandoClientePremiumeNotificadoPorCotacaoVariarMaisDe10PorCento() throws Exception{

            clienteService.marcarInteresseAtivoDisponivel(clientePremium.getId(), clientePremium.getCodigo(), ativoDisponivel.getId());

            BigDecimal novaCotacao = new BigDecimal("110.00");

            driver.perform(put(uriAtivos + "/" + ativoDisponivel.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", novaCotacao.toString()))
                    .andExpect(status().isOk());

            List<ILoggingEvent> logsList = listAppender.list;
            assertEquals(1, logsList.size());
            String logMessage = logsList.get(0).getFormattedMessage();

            assertTrue(logMessage.contains("Caro cliente " + clientePremium.getNome() + ", o ativo que você marcou interesse, teve uma taxa de variação de cotação acima de 10%!"));
            assertTrue(logMessage.contains("Nome do ativo: " + ativoDisponivel.getNome()));
            assertTrue(logMessage.contains("O tipo do ativo: " + ativoDisponivel.getTipo().toString()));
            assertTrue(logMessage.contains("Cotação do ativo: " + ativoDisponivel.getCotacao()));
            assertTrue(logMessage.contains("Descrição do ativo: " + ativoDisponivel.getDescricao()));
        }

        @Test
        @DisplayName("Notificar cliente Premium quando cotação de ativo de interesse varia mais de 10%, MAS ELE NÃO MARCOU INTERESSE")
        void quandoClienteNaoMarcaInteresseEmAtivoDisponiveleNaoRecebeNotificacaoQuandoEleVariaMaisDe10PorCento() throws Exception{

            BigDecimal novaCotacao = new BigDecimal("110.00");

            driver.perform(put(uriAtivos + "/" + ativoDisponivel.getId() + "/cotacao")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("novoValor", novaCotacao.toString()))
                    .andExpect(status().isOk());

            List<ILoggingEvent> logsList = listAppender.list;
            assertEquals(0, logsList.size());
        }

        @Test
        @DisplayName("Cliente Normal não pode registrar interesse em ativo disponível (deve lançar exceção)")
        void quandoClienteNormalTentaRegistrarInteresseEmAtivoDisponiveleNaoConsegue() throws Exception{

            driver.perform(put(uriClientes + "/" + cliente.getId() + "/interesseAtivoDisponivel" + "/" + ativoDisponivel.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Cliente Premium não pode registrar interesse em ativo indisponível pelo metodo de ativo disponível(deve lançar exceção)")
        void quandoClientePremiumTentaRegistrarInteresseemUmAtivoIndisponivelPeloMetododeAtivoDisponivel() throws Exception{

            driver.perform(put(uriClientes + "/" + clientePremium.getId() + "/interesseAtivoDisponivel" + "/" + ativoIndisponivel.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", clientePremium.getCodigo()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Cliente (NORMAL) com interesse em ativo indisponivel que não seja tesouro direto")
        void quandoClienteNormalTentaMarcarInteresseEmAtivoIndisponivelQueNaoSejaTesouroDireto() throws Exception{

            driver.perform(put(uriClientes + "/" + cliente.getId() + "/interesseAtivoIndisponivel" + "/" + ativoIndisponivel.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Cliente (PREMIUM) com interesse deve ser notificado quando ativo fica disponível")
        void quandoClientePremiumMarcaInteresseEmAtivoIndisponivelFicaDisponivelDeveSerNotificado() throws Exception{

            clienteService.marcarInteresseAtivoIndisponivel(clientePremium.getId(), clientePremium.getCodigo(), ativoIndisponivel.getId());

            driver.perform(put(uriAtivos + "/" + ativoIndisponivel.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isOk());

            List<ILoggingEvent> logsList = listAppenderAtivoDisponivel.list;
            assertEquals(1, logsList.size());
            String logMessage = logsList.get(0).getFormattedMessage();

            assertTrue(logMessage.contains("Caro cliente " + clientePremium.getNome() + ", o ativo indisponível que você marcou interesse está disponível!"));
            assertTrue(logMessage.contains("Dados do Ativo:"));
            assertTrue(logMessage.contains("Nome do ativo: " + ativoIndisponivel.getNome()));
            assertTrue(logMessage.contains("O tipo do ativo: " + ativoIndisponivel.getTipo().toString()));
            assertTrue(logMessage.contains("Cotação do ativo: " + ativoIndisponivel.getCotacao().toString()));
            assertTrue(logMessage.contains("Descrição do ativo: " + ativoIndisponivel.getDescricao()));
        }

        @Test
        @DisplayName("Nenhuma notificação deve ser enviada se ninguém tiver interesse no ativo")
        void nenhumaNotificacaoDeveChegarQuandoNinguemTiverInteresseNoAtivo() throws Exception{

            driver.perform(put(uriAtivos + "/" + ativoIndisponivel.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isOk());

            assertTrue(listAppenderAtivoDisponivel.list.isEmpty(), "Nenhuma notificação deveria ter sido gerada.");
        }

        @Test
        @DisplayName("Deve falhar ao tentar registrar interesse em ativo que já está disponível")
        void quandoTentamosRegistrarInteresseEmAtivoDisponivelPeloMetodoDeAtivoIndisponivel() throws Exception{

            driver.perform(put(uriClientes + "/" + clientePremium.getId() + "/interesseAtivoIndisponivel" + "/" + ativoDisponivel.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", clientePremium.getCodigo()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da vizualização detalhada de ativos")
    class ClienteVizualizaoAtivoDetalhada {

        @Test
        @DisplayName("quando cliente busca por um ativo com seus dados válidos")
        void QuandoClienteBuscaAtivoDadosValidos() throws Exception {
            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/detalharAtivo/" + ativo1.getId())
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(ativo1.getNome(), resultado.getNome());
        }


        @Test
        @DisplayName("quando cliente busca por um ativo inexistente")
        void QuandoClienteBuscaAtivoInexistente() throws Exception {
            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/detalharAtivo/" + 999999999)
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("quando cliente busca por um ativo com seu código de acesso inválido")
        void QuandoClienteBuscaAtivoCodigoAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/detalharAtivo/" + ativo1.getId())
                            .param("codigo", "999999999"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("quando cliente não premium tenta buscar um ativo que não está disponivel para seu plano")
        void QuandoClienteNormalBuscaAtivoIndisponvelParaSeuPlano() throws Exception {
            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/detalharAtivo/" + ativo2.getId())
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Esta funcionalidade esta disponivel apenas para clientes Premium.", resultado.getMessage());
        }

        @Test
        @DisplayName("quando cliente premium busca por um ativo disponivel apenas para plano premium")
        void QuandoClientePremiumBuscaAtivoDisponivelParaSeuPlanoPremium() throws Exception {
            cliente.setPlano(TipoPlano.PREMIUM);
            clienteRepository.save(cliente);

            String responseJsonString = driver.perform(get(uriClientes + "/" + cliente.getId() + "/detalharAtivo/" + ativo2.getId())
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AtivoResponseDTO resultado = objectMapper.readValue(responseJsonString, AtivoResponseDTO.class);

            assertEquals(ativo2.getNome(), resultado.getNome());
        }
    }
}