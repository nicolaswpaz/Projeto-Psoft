package com.ufcg.psoft.commerce.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoPremiumException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.exception.compra.QuantidadeInvalidaException;
import com.ufcg.psoft.commerce.listener.NotificacaoCompraDisponivel;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.compra.CompraResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.exception.administrador.MatriculaInvalidaException;
import com.ufcg.psoft.commerce.exception.compra.CompraNaoExisteException;
import com.ufcg.psoft.commerce.exception.compra.StatusCompraInvalidoException;
import com.ufcg.psoft.commerce.exception.conta.SaldoInsuficienteException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
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
import java.math.RoundingMode;
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

    final String uriCompras = "/compras";
    final String uriClientes = "/clientes";

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
    ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    @Transactional
    void setup() {

        Logger logger = (Logger) LoggerFactory.getLogger(NotificacaoCompraDisponivel.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

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
                Conta.builder().saldo(BigDecimal.valueOf(10000.0)).carteira(new Carteira()).build()
        );

        contaClientePremium = contaRepository.save(
                Conta.builder().saldo(BigDecimal.valueOf(500.0)).carteira(new Carteira()).build()
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

        contaClienteNormal.setCliente(clienteNormal);
        contaRepository.save(contaClienteNormal);

        clientePremium = clienteRepository.save(Cliente.builder()
                .nome("Cliente Premium da Silva")
                .endereco(enderecoClientePremium)
                .cpf("01987654321")
                .codigo("123456")
                .plano(TipoPlano.PREMIUM)
                .conta(contaClientePremium)
                .build()
        );

        contaClientePremium.setCliente(clientePremium);
        contaRepository.save(contaClientePremium);

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
    }

    @Nested
    @DisplayName("Fluxo de solicitação de compras pelo cliente")
    class FluxoSolicitacaoCompraPeloCliente {

        @Test
        @DisplayName("Solicitar compra com cliente inexistente deve falhar")
        void solicitarCompraClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(post(uriCompras + "/99999/" + ativoTesouro.getId())
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
            String responseJsonString = driver.perform(post(uriCompras + "/" + clienteNormal.getId() + "/99999")
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
            String responseJsonString = driver.perform(post(uriCompras + "/" + clienteNormal.getId() + "/" + ativoTesouro.getId())
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
            String responseJsonString = driver.perform(post(uriCompras + "/" + clienteNormal.getId() + "/" + ativoTesouro.getId())
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
            String responseJsonString = driver.perform(post(uriCompras + "/" + clienteNormal.getId() + "/" + ativoAcao.getId())
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
            String responseJsonString = driver.perform(post(uriCompras + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
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
            String responseJsonString = driver.perform(post(uriCompras + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
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
    @DisplayName("Fluxo de disponibilização de compras pelo administrador")
    class FluxoDisponibilizarCompra {

        @Test
        @DisplayName("Administrador disponibiliza compra com sucesso e cliente é notificado")
        void disponibilizarCompraSucesso() throws Exception {
            Long idCliente = clientePremium.getId();
            String codigoCliente = clientePremium.getCodigo();
            Long idAtivo = ativoAcao.getId();
            String matriculaAdmin = administrador.getMatricula();

            if (clientePremium.getConta().getCarteira() == null) {
                clientePremium.getConta().setCarteira(new Carteira());
            }

            CompraResponseDTO novaCompra = compraService.solicitarCompra(idCliente, codigoCliente, idAtivo, 2);

            String responseJsonString = driver.perform(put(uriCompras + "/admin/" + novaCompra.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaAdmin))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            CompraResponseDTO compra = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

            assertNotNull(compra);
            assertEquals(novaCompra.getId(), compra.getId());
            assertEquals(StatusCompra.DISPONIVEL, compra.getStatusCompra());
            assertEquals(idCliente, compra.getCliente().getId());
            assertEquals(idAtivo, compra.getAtivo().getId());
        }

        @Test
        @DisplayName("O cliente deve ser notificado após ter sua compra disponibilizada")
        void notificaDisponibilidadeDeCompra() throws Exception {
            Long idCliente = clientePremium.getId();
            String codigoCliente = clientePremium.getCodigo();
            Long idAtivo = ativoAcao.getId();
            String matriculaAdmin = administrador.getMatricula();

            CompraResponseDTO novaCompra = compraService.solicitarCompra(idCliente, codigoCliente, idAtivo, 2);

            driver.perform(put(uriCompras + "/admin/" + novaCompra.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaAdmin))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            List<ILoggingEvent> logsList = listAppender.list;
            assertEquals(1, logsList.size());
            String logMessage = logsList.get(0).getFormattedMessage();

            assertTrue(logMessage.contains("Caro cliente " + clientePremium.getNome() + ", a compra que você solicitou, está disponível!"));
            assertTrue(logMessage.contains("Nome do ativo comprado: " + ativoAcao.getNome()));
            assertTrue(logMessage.contains("O tipo do ativo comprado: " + ativoAcao.getTipo().toString()));
            assertTrue(logMessage.contains("Valor total da compra: " + novaCompra.getValorVenda()));
            assertTrue(logMessage.contains("Valor do ativo no momento da compra: " + novaCompra.getValorVenda().divide(BigDecimal.valueOf(novaCompra.getQuantidade()), RoundingMode.HALF_UP)));
            assertTrue(logMessage.contains("Quantidade de ativos comprados " + novaCompra.getQuantidade()));
        }

        @Test
        @DisplayName("Tentativa de disponibilizar compra inexistente lança exceção")
        void disponibilizarCompraNaoExiste() {
            String matriculaAdmin = administrador.getMatricula();
            Long idInvalido = 99999L;

            assertThrows(CompraNaoExisteException.class, () -> {
                compraService.disponibilizarCompra(idInvalido, matriculaAdmin);
            });
        }

        @Test
        @DisplayName("Tentativa de disponibilizar compra com status inválido lança exceção")
        void disponibilizarCompraStatusInvalido() {
            Long clientePremiumId = clientePremium.getId();
            String clientePremiumCodigo = clientePremium.getCodigo();
            Long ativoId = ativoAcao.getId();

            CompraResponseDTO novaCompra = compraService.solicitarCompra(
                    clientePremiumId, clientePremiumCodigo, ativoId, 1
            );

            String matriculaAdmin = administrador.getMatricula();
            Long compraId = novaCompra.getId();

            compraService.disponibilizarCompra(compraId, matriculaAdmin);

            assertThrows(StatusCompraInvalidoException.class, () ->
                    compraService.disponibilizarCompra(compraId, matriculaAdmin)
            );
        }

        @Test
        @DisplayName("Confirmar disponibilidade de compra com saldo insuficiente deve lançar exceção")
        void confirmarDisponibilidadeCompra_SaldoInsuficiente() {
            Long clientePremiumId = clientePremium.getId();
            String clientePremiumCodigo = clientePremium.getCodigo();
            Long ativoId = ativoAcao.getId();

            CompraResponseDTO novaCompra = compraService.solicitarCompra(
                    clientePremiumId, clientePremiumCodigo, ativoId, 1
            );

            clientePremium.getConta().setSaldo(new BigDecimal(0));

            String matriculaAdmin = administrador.getMatricula();
            Long compraId = novaCompra.getId();

            assertThrows(SaldoInsuficienteException.class,
                    () -> compraService.disponibilizarCompra(compraId, matriculaAdmin)
            );
        }


        @Test
        @DisplayName("Confirmar disponibilidade de compra com matrícula inválida deve lançar exceção")
        void confirmarDisponibilidadeCompraMatriculaInvalida() {
            Long clientePremiumId = clientePremium.getId();
            String clientePremiumCodigo = clientePremium.getCodigo();
            Long ativoId = ativoAcao.getId();

            CompraResponseDTO novaCompra = compraService.solicitarCompra(
                    clientePremiumId, clientePremiumCodigo, ativoId, 1
            );

            Long compraId = novaCompra.getId();

            assertThrows(MatriculaInvalidaException.class,
                    () -> compraService.disponibilizarCompra(compraId, "MatriculaErrada:C")
            );
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

            String responseJsonString = driver.perform(get(uriClientes + "/" + idCliente + "/carteira")
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

            String responseJsonString = driver.perform(get(uriClientes + "/" + idCliente + "/carteira")
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
                clienteNormal.getConta().getCarteira().getAtivosEmCarteira().clear();
            }

            String responseJsonString = driver.perform(get(uriClientes + "/" + idCliente + "/carteira")
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

    @Nested
    @DisplayName("Fluxo dos estados da compra")
    class FluxoEstadosDaCompra {

        @Test
        @DisplayName("Solicitar compra deve funcionar")
        void solicitarCompraClienteNormalDeveFuncionar() throws Exception {
            String responseJsonString = driver.perform(post(uriCompras + "/" + clienteNormal.getId() + "/" + ativoTesouro.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clienteNormal.getCodigo())
                            .param("quantidade", "3"))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CompraResponseDTO compra = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);


            assertEquals(clienteNormal.getId(), compra.getCliente().getId());
            assertEquals(ativoTesouro.getId(), compra.getAtivo().getId());
            assertEquals(StatusCompra.SOLICITADO, compra.getStatusCompra());
        }

        @Test
        @DisplayName("Disponibilizar compra cliente normal deve funcionar")
        void disponibilizarCompraClienteNormalDeveFuncionar() throws Exception {
            CompraResponseDTO compra = compraService.solicitarCompra(
                    clienteNormal.getId(), clienteNormal.getCodigo(), ativoTesouro.getId(), 1);

            String responseJsonString = driver.perform(put(uriCompras + "/admin/" + compra.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CompraResponseDTO compraDisponibilizada = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);


            assertEquals(StatusCompra.DISPONIVEL, compraDisponibilizada.getStatusCompra());
        }

        @Test
        @DisplayName("Confirmar compra deve mudar para CONFIRMADO")
        void confirmarCompraClienteNormalDeveFuncionar() throws Exception {
            CompraResponseDTO compra = compraService.solicitarCompra(clienteNormal.getId(), clienteNormal.getCodigo(), ativoTesouro.getId(), 1);
            compraService.disponibilizarCompra(compra.getId(), administrador.getMatricula());


            String responseJsonString = driver.perform(put(uriCompras + "/" + clienteNormal.getId() + "/" + compra.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clienteNormal.getCodigo()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CompraResponseDTO compraConfirmada = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);


            assertEquals(StatusCompra.EM_CARTEIRA, compraConfirmada.getStatusCompra());
        }

        @Test
        @DisplayName("Solicitar compra deve funcionar")
        void solicitarCompraClientePremiumDeveFuncionar() throws Exception {
            String responseJsonString = driver.perform(post(uriCompras + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("quantidade", "2"))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CompraResponseDTO compra = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

            assertEquals(clientePremium.getId(), compra.getCliente().getId());
            assertEquals(ativoAcao.getId(), compra.getAtivo().getId());
            assertEquals(StatusCompra.SOLICITADO, compra.getStatusCompra());
        }

        @Test
        @DisplayName("Disponibilizar compra cliente premium deve funcionar")
        void disponibilizarCompraClientePremiumDeveFuncionar() throws Exception {

            CompraResponseDTO compra = compraService.solicitarCompra(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            String responseJsonString = driver.perform(put(uriCompras + "/admin/" + compra.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CompraResponseDTO compraDisponibilizada = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);


            assertEquals(StatusCompra.DISPONIVEL, compraDisponibilizada.getStatusCompra());
        }

        @Test
        @DisplayName("Confirmar compra deve mudar para CONFIRMADO")
        void confirmarCompraClientePremiumDeveFuncionar() throws Exception {
            CompraResponseDTO compra = compraService.solicitarCompra(clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 2);
            compraService.disponibilizarCompra(compra.getId(), administrador.getMatricula());


            String responseJsonString = driver.perform(put(uriCompras + "/" + clientePremium.getId() + "/" + compra.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();


            CompraResponseDTO compraConfirmada = objectMapper.readValue(responseJsonString, CompraResponseDTO.class);

            assertEquals(StatusCompra.EM_CARTEIRA, compraConfirmada.getStatusCompra());
        }

        @Test
        @DisplayName("Confirmar compra com status inválido deve lançar exceção")
        void confirmarCompraStatusInvalido() {
            Long idCliente = clientePremium.getId();
            String codigoCliente = clientePremium.getCodigo();
            Long idAtivo = ativoAcao.getId();

            CompraResponseDTO novaCompra = compraService.solicitarCompra(idCliente, codigoCliente, idAtivo, 1);
            Long idCompra = novaCompra.getId();

            assertThrows(StatusCompraInvalidoException.class, () ->
                    compraService.confirmarCompra(idCliente, codigoCliente, idCompra)
            );
        }

        @Test
        @DisplayName("Listar compras com matrícula admin deve funcionar")
        void listarComprasAdminDeveFuncionar() throws Exception {
            compraService.solicitarCompra(clienteNormal.getId(), clienteNormal.getCodigo(), ativoTesouro.getId(), 1);
            compraService.solicitarCompra(clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            String responseJson = driver.perform(get(uriCompras + "/admin/" + administrador.getMatricula())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJson.contains("\"id\""));
            assertTrue(responseJson.contains("\"ativo\""));
        }

        @Test
        @DisplayName("Disponibilizar compra com status diferente de SOLICITADO deve lançar exceção")
        void disponibilizarCompraStatusInvalido() {
            CompraResponseDTO compra = compraService.solicitarCompra(
                    clienteNormal.getId(),
                    clienteNormal.getCodigo(),
                    ativoTesouro.getId(),
                    1
            );
            compraService.disponibilizarCompra(compra.getId(), administrador.getMatricula());

            Long compraId = compra.getId();
            String adminMatricula = administrador.getMatricula();

            assertThrows(StatusCompraInvalidoException.class, () ->
                    compraService.disponibilizarCompra(compraId, adminMatricula)
            );
        }

        @Test
        @DisplayName("Consultar compra de outro cliente deve lançar exceção")
        void consultarCompraOutroCliente() {
            Long clienteNormalId = clienteNormal.getId();
            String clienteNormalCodigo = clienteNormal.getCodigo();
            Long ativoId = ativoTesouro.getId();

            CompraResponseDTO compra = compraService.solicitarCompra(
                    clienteNormalId, clienteNormalCodigo, ativoId, 1
            );

            Long clientePremiumId = clientePremium.getId();
            String clientePremiumCodigo = clientePremium.getCodigo();
            Long compraId = compra.getId();

            assertThrows(CompraNaoPertenceAoClienteException.class, () ->
                    compraService.consultar(clientePremiumId, clientePremiumCodigo, compraId)
            );
        }

        @Test
        @DisplayName("Consultar compra inexistente deve lançar exceção")
        void consultarCompraInexistente() {
            Long clienteId = clienteNormal.getId();
            String clienteCodigo = clienteNormal.getCodigo();

            assertThrows(CompraNaoExisteException.class, () ->
                    compraService.consultar(clienteId, clienteCodigo, 999L)
            );
        }

        @Test
        @DisplayName("Listar compras com matrícula inválida deve lançar exceção")
        void listarComprasMatriculaInvalida() {
            assertThrows(RuntimeException.class, () -> {
                compraService.listar("matricula_invalida");
            });
        }

        @Test
        @DisplayName("Solicitar compra com cliente normal para ativo não tesouro deve lançar exceção")
        void solicitarCompraNormalAtivoNaoTesouro() {
            Long clienteId = clienteNormal.getId();
            String clienteCodigo = clienteNormal.getCodigo();
            Long ativoId = ativoAcao.getId();

            assertThrows(ClienteNaoPremiumException.class, () ->
                    compraService.solicitarCompra(clienteId, clienteCodigo, ativoId, 1)
            );
        }

        @Test
        @DisplayName("Solicitar compra com cliente inexistente deve lançar exceção")
        void solicitarCompraClienteInexistente() {
            Long ativoId = ativoTesouro.getId();

            assertThrows(RuntimeException.class, () ->
                    compraService.solicitarCompra(999L, "codigo_invalido", ativoId, 1)
            );
        }

        @Test
        @DisplayName("Confirmar compra já em carteira deve lançar exceção")
        void confirmarCompraJaEmCarteira() {
            CompraResponseDTO compra = compraService.solicitarCompra(
                    clienteNormal.getId(),
                    clienteNormal.getCodigo(),
                    ativoTesouro.getId(),
                    1
            );
            compraService.disponibilizarCompra(compra.getId(), administrador.getMatricula());
            compraService.confirmarCompra(clienteNormal.getId(), clienteNormal.getCodigo(), compra.getId());

            Long clienteId = clienteNormal.getId();
            String clienteCodigo = clienteNormal.getCodigo();
            Long compraId = compra.getId();

            assertThrows(StatusCompraInvalidoException.class, () ->
                    compraService.confirmarCompra(clienteId, clienteCodigo, compraId)
            );
        }

        @Test
        @DisplayName("Confirmar compra de outro cliente deve lançar exceção")
        void confirmarCompraOutroCliente() {
            CompraResponseDTO compra = compraService.solicitarCompra(
                    clienteNormal.getId(),
                    clienteNormal.getCodigo(),
                    ativoTesouro.getId(),
                    1
            );
            compraService.disponibilizarCompra(compra.getId(), administrador.getMatricula());

            Long clientePremiumId = clientePremium.getId();
            String clientePremiumCodigo = clientePremium.getCodigo();
            Long compraId = compra.getId();

            assertThrows(CompraNaoPertenceAoClienteException.class, () ->
                    compraService.confirmarCompra(clientePremiumId, clientePremiumCodigo, compraId)
            );
        }

        @Test
        @DisplayName("Disponibilizar compra com matrícula de admin inválida via controller deve retornar 400")
        void disponibilizarCompraMatriculaInvalidaController() throws Exception {
            CompraResponseDTO compra = compraService.solicitarCompra(clienteNormal.getId(), clienteNormal.getCodigo(), ativoTesouro.getId(), 1);

            driver.perform(put(uriCompras + "/admin/" + compra.getId() + "/disponibilizar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", "matricula_invalida"))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("Solicitar compra com quantidade zero deve lançar exceção")
        void solicitarCompraQuantidadeZero() {
            Long clienteId = clienteNormal.getId();
            String clienteCodigo = clienteNormal.getCodigo();
            Long ativoId = ativoTesouro.getId();

            assertThrows(QuantidadeInvalidaException.class, () ->
                    compraService.solicitarCompra(clienteId, clienteCodigo, ativoId, 0)
            );
        }

        @Test
        @DisplayName("Confirmar compra com código de acesso inválido via controller deve retornar 400")
        void confirmarCompraCodigoAcessoInvalidoController() throws Exception {
            CompraResponseDTO compra = compraService.solicitarCompra(clienteNormal.getId(), clienteNormal.getCodigo(), ativoTesouro.getId(), 1);
            compraService.disponibilizarCompra(compra.getId(), administrador.getMatricula());

            driver.perform(put(uriCompras + "/" + clienteNormal.getId() + "/" + compra.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", "codigo_invalido"))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}