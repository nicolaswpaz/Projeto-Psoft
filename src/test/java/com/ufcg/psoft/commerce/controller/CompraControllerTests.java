package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.compra.CompraService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes do controlador de Compras")
class CompraControllerTests {

    final String URI_COMPRAS = "/compras";
    final String URI_CLIENTES = "/clientes";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ContaRepository contaRepository;

    @Autowired
    AtivoRepository ativoRepository;

    @Autowired
    AdministradorRepository administradorRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    AtivoCarteiraRepository ativoCarteiraRepository;

    @Autowired
    CompraService compraService;

    Administrador administrador;
    Cliente clienteNormal;
    Cliente clientePremium;
    Conta contaClienteNormal;
    Conta contaClientePremium;
    Ativo ativoTesouro;
    Ativo ativoAcao;
    Endereco enderecoClienteNormal;
    Endereco enderecoClientePremium;

    @BeforeEach
    @Transactional
    void setup() {

        objectMapper.registerModule(new JavaTimeModule());

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

        contaClienteNormal = contaRepository.save(
                Conta.builder().saldo(BigDecimal.valueOf(10000.0)).build()
        );

        contaClientePremium = contaRepository.save(
                Conta.builder().saldo(BigDecimal.valueOf(500.0)).build()
        );

        enderecoClienteNormal = enderecoRepository.save(Endereco.builder()
                .rua("Rua dos testes")
                .bairro("Bairro testado")
                .numero("123")
                .complemento("")
                .cep("58400-000")
                .build());

        enderecoClientePremium = enderecoRepository.save(Endereco.builder()
                .rua("Rua dos testes2")
                .bairro("Bairro testado")
                .numero("321")
                .complemento("")
                .cep("58300-000")
                .build());

        clienteNormal = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(enderecoClienteNormal)
                .plano(TipoPlano.NORMAL)
                .cpf("12345678910")
                .codigo("123456")
                .conta(contaClienteNormal)
                .build()
        );

        clientePremium = clienteRepository.save(Cliente.builder()
                .nome("Cliente Premium da Silva")
                .endereco(enderecoClientePremium)
                .cpf("01987654321")
                .codigo("123456")
                .plano(TipoPlano.PREMIUM)
                .conta(contaClientePremium)
                .build()
        );

        ativoTesouro = ativoRepository.save(Ativo.builder()
                .nome("Tesouro Teste")
                .tipo(TipoAtivo.TESOURO_DIRETO)
                .cotacao(BigDecimal.valueOf(100.0))
                .disponivel(true)
                .descricao("Ativo Tesouro")
                .build());

        ativoAcao = ativoRepository.save(Ativo.builder()
                .nome("Acao Teste")
                .tipo(TipoAtivo.ACAO)
                .cotacao(BigDecimal.valueOf(50.0))
                .disponivel(true)
                .descricao("Ativo Acao")
                .build());
    }

    @AfterEach
    void tearDown() {
        ativoRepository.deleteAll();
        clienteRepository.deleteAll();
        contaRepository.deleteAll();
    }

    @Nested
    @DisplayName("Fluxo de solicitação de compras pelo cliente")
    class FluxoSolicitacaoCompraPeloCliente {

        @Test
        @DisplayName("Solicitar compra com cliente inexistente deve falhar")
        void solicitarCompraClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/99999/" + ativoTesouro.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "qualquer")
                            .param("quantidade", "1"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Solicitar compra com ativo inexistente deve falhar")
        void solicitarCompraAtivoInexistente() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/" + clienteNormal.getId() + "/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clienteNormal.getCodigo())
                            .param("quantidade", "1"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O ativo consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Solicitar compra com código de acesso incorreto deve falhar")
        void solicitarCompraCodigoInvalido() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/" + clienteNormal.getId() + "/" + ativoTesouro.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "errado123")
                            .param("quantidade", "1"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }


        @Test
        @DisplayName("Solicitar compra de ativo TESOURO com cliente normal deve criar compra")
        void solicitarCompraClienteNormal() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/" + clienteNormal.getId() + "/" + ativoTesouro.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clienteNormal.getCodigo())
                            .param("quantidade", "2"))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CompraResponseDTO compra = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

            assertEquals(clienteNormal.getId(), compra.getCliente().getId());
            assertEquals(ativoTesouro.getId(), compra.getAtivo().getId());
            assertEquals(2, compra.getQuantidade());
        }

        @Test
        @DisplayName("Solicitar compra de AÇÃO com cliente normal deve falhar")
        void solicitarCompraClienteNormalAtivoAcao() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/" + clienteNormal.getId() + "/" + ativoAcao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clienteNormal.getCodigo())
                            .param("quantidade", "5"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Esta funcionalidade esta disponivel apenas para clientes Premium.", resultado.getMessage());
        }

        @Test
        @DisplayName("Solicitar compra de um ativo AÇÃO com cliente premium deve funcionar")
        void solicitarCompraClientePremium() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("quantidade", "3"))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CompraResponseDTO compra = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

            assertEquals(clientePremium.getId(), compra.getCliente().getId());
            assertEquals(ativoAcao.getId(), compra.getAtivo().getId());
        }

        @Test
        @DisplayName("O CLIENTE pode solicitar uma compra de valor maior que seu saldo")
        void solicitarCompraSaldoInsuficiente() throws Exception {
            String responseJsonString = driver.perform(post(URI_COMPRAS + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("quantidade", "9999"))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CompraResponseDTO compra = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

            assertEquals(clientePremium.getId(), compra.getCliente().getId());
            assertEquals(ativoAcao.getId(), compra.getAtivo().getId());
            assertEquals(9999, compra.getQuantidade());
        }
    }

    @Nested
    @DisplayName("Fluxo da carteira do cliente")
    class FluxoCarteiraCliente {

        @Test
        @DisplayName("Visualizar carteira de cliente inexistente deve falhar")
        void visualizarCarteiraClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(get("/clientes/99999/carteira")
                            .param("codigoAcesso", "qualquer")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Visualizar carteira com código de acesso inválido deve falhar")
        void visualizarCarteiraCodigoInvalido() throws Exception {
            Long idCliente = clientePremium.getId();
            String codigoCliente = clientePremium.getCodigo();
            Long idAtivo = ativoAcao.getId();
            String matriculaAdmin = administrador.getMatricula();

            if (clientePremium.getConta().getCarteira() == null) {
                clientePremium.getConta().setCarteira(new Carteira());
            }

            CompraResponseDTO novaCompra = compraService.solicitarCompra(idCliente, codigoCliente, idAtivo, 2);
            compraService.disponibilizarCompra(novaCompra.getId(), matriculaAdmin);
            compraService.confirmarCompra(idCliente, codigoCliente, novaCompra.getId());

            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + idCliente + "/carteira")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "000000"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Cliente visualiza carteira após comprar ativos")
        void visualizarCarteiraAposCompra() throws Exception {
            Long idCliente = clientePremium.getId();
            String codigoCliente = clientePremium.getCodigo();
            Long idAtivo = ativoAcao.getId();
            String matriculaAdmin = administrador.getMatricula();

            if (clientePremium.getConta().getCarteira() == null) {
                clientePremium.getConta().setCarteira(new Carteira());
            }

            CompraResponseDTO novaCompra = compraService.solicitarCompra(idCliente, codigoCliente, idAtivo, 2);
            compraService.disponibilizarCompra(novaCompra.getId(), matriculaAdmin);
            compraService.confirmarCompra(idCliente, codigoCliente, novaCompra.getId());

            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + idCliente + "/carteira")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", codigoCliente))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<AtivoEmCarteiraResponseDTO> carteira = objectMapper.readValue(
                    responseJsonString,
                    new TypeReference<List<AtivoEmCarteiraResponseDTO>>() {}
            );

            assertNotNull(carteira, "Carteira não pode ser nula");
            assertFalse(carteira.isEmpty(), "Carteira não pode estar vazia");
            assertEquals(1, carteira.size(), "Deve haver 1 ativo na carteira");

            AtivoEmCarteiraResponseDTO item = carteira.get(0);
            assertEquals("Acao Teste", item.getNomeAtivo());
            assertEquals(TipoAtivo.ACAO, item.getTipo());
            assertEquals(2, item.getQuantidadeTotal());
            assertEquals(BigDecimal.valueOf(50.0), item.getValorDeAquisicao());
            assertEquals(BigDecimal.valueOf(100.0), item.getValorAtual());
            assertEquals(BigDecimal.valueOf(50.0), item.getDesempenho());
        }

        @Test
        @DisplayName("Cliente visualiza carteira vazia")
        void visualizarCarteiraVazia() throws Exception {
            Long idCliente = clienteNormal.getId();
            String codigoCliente = clienteNormal.getCodigo();

            if (clienteNormal.getConta().getCarteira() == null) {
                clienteNormal.getConta().setCarteira(new Carteira());
            } else {
                clienteNormal.getConta().getCarteira().getAtivoEmCarteiras().clear();
            }

            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + idCliente + "/carteira")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", codigoCliente))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<AtivoEmCarteiraResponseDTO> carteira = objectMapper.readValue(
                    responseJsonString,
                    new TypeReference<List<AtivoEmCarteiraResponseDTO>>() {}
            );

            assertNotNull(carteira, "Carteira não pode ser nula");
            assertTrue(carteira.isEmpty(), "Carteira deve estar vazia");
        }
    }
}