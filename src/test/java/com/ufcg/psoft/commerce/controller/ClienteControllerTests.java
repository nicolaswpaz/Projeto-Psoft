package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EnderecoRepository;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.transaction.Transactional;
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
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

    final String URI_CLIENTES = "/clientes";
    final String URI_ATIVOS = "/ativos";

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    AtivoRepository ativoRepository;
    Ativo ativo1;
    Ativo ativo2;
    Ativo ativo3;
    AtivoPostPutRequestDTO ativoPostPutRequestDTO;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;

    ClientePostPutRequestDTO clientePostPutRequestDTO;

    @Autowired
    EnderecoRepository enderecoRepository;

    Endereco endereco;
    EnderecoResponseDTO enderecoDTO;
    @Autowired
    private ClienteService clienteService;

    @BeforeEach
    @Transactional
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        endereco = /*enderecoRepository.save(*/Endereco.builder()
                .rua("Rua dos testes")
                .bairro("Bairro testado")
                .numero("123")
                .complemento("")
                .cep("58400-000")
                .build();
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .cpf("12345678910")
                .codigo("123456")
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
        ativoRepository.saveAll(Arrays.asList(ativo1, ativo2, ativo3));
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
        ativoRepository.deleteAll();
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
                    .complemento("Novo Complemento")
                    .bairro("Novo Bairro")
                    .numero("2")
                    .cep("155427-000").build();

            clientePostPutRequestDTO.setEnderecoDTO(novoEndereco);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo())
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert - Compare apenas os campos relevantes
            assertEquals(novoEndereco.getRua(), resultado.getEndereco().getRua());
            assertEquals(novoEndereco.getComplemento(), resultado.getEndereco().getComplemento());
            assertEquals(novoEndereco.getBairro(), resultado.getEndereco().getBairro());
            assertEquals(novoEndereco.getNumero(), resultado.getEndereco().getNumero());
            assertEquals(novoEndereco.getCep(), resultado.getEndereco().getCep());
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
            clientePostPutRequestDTO.setEnderecoDTO(new EnderecoResponseDTO());

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
                    () -> assertTrue(resultado.getErrors().contains("Rua obrigatoria")),
                    () -> assertTrue(resultado.getErrors().contains("Bairro obrigatorio")),
                    () -> assertTrue(resultado.getErrors().contains("Numero obrigatorio")),
                    () -> assertTrue(resultado.getErrors().contains("CEP obrigatorio"))
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
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
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

            // Assert
            assertAll(
                    () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals("987654", cliente.getCodigo())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class ClienteVerificacaoFluxosBasicosApiRest {
/*
        @Test
        @DisplayName("Quando buscamos por todos clientes salvos")
        void quandoBuscamosPorTodosClienteSalvos() throws Exception {
            // Arrange
            // Vamos ter 3 clientes no banco
            Cliente cliente1 = Cliente.builder()
                    .nome("Cliente Dois Almeida")
                    .endereco("Av. da Pits A, 100")
                    .codigo("246810")
                    .build();
            Cliente cliente2 = Cliente.builder()
                    .nome("Cliente Três Lima")
                    .endereco("Distrito dos Testadores, 200")
                    .codigo("135790")
                    .build();
            clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size())
            );
        }
*/
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
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + 999999999)
                            .contentType(MediaType.APPLICATION_JSON)
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
                    .nome("")
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
                    () -> assertEquals("Nome obrigatorio", resultado.getMessage())
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

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
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

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(clientePostPutRequestDTO.getNome(), resultado.getNome())
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
        @DisplayName("Quando criamos um novo cliente com codigo null")
        void quandoCriarClienteCodigoNull() throws Exception {
            // Arrange
            clientePostPutRequestDTO = ClientePostPutRequestDTO.builder()
                    .nome("Cliente sem Codigo")
                    .enderecoDTO(enderecoDTO)
                    .codigo(null)
                    .cpf("12345678910")
                    .build();

            // Act
            String responseJsonString = driver.perform(post(URI_CLIENTES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientePostPutRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage())
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
        @DisplayName("Quando excluímos um cliente salvo")
        void quandoExcluimosClienteValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigo", cliente.getCodigo()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
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

        @Test
        @DisplayName("Quando listamos ativos disponiveis passando o id de um cliente válido com plano Normal")
        void quandoListamosAtivosClientePlanoNormal() throws Exception {
            //Arrange
            cliente.setPlano(TipoPlano.NORMAL);
            clienteRepository.save(cliente);

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
            assertEquals(nomesRetornados.size(), 1);
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
            assertEquals(nomesRetornados.size(), 2);
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
    }
}
