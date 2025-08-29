package com.ufcg.psoft.commerce.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.listener.NotificacaoCompraDisponivel;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.service.operacao.compra.CompraService;
import com.ufcg.psoft.commerce.service.operacao.resgate.ResgateService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes do controlador de Resgates")
class ResgateControllerTests {

    final String uriResgates = "/resgates";

    @Autowired
    MockMvc driver;

    @Autowired
    ObjectMapper objectMapper;

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
    CompraRepository compraRepository;

    @Autowired
    CompraService compraService;

    @Autowired
    ResgateService resgateService;

    Administrador administrador;
    Cliente clienteNormal;
    Cliente clientePremium;
    Conta contaClienteNormal;
    Conta contaClientePremium;
    Ativo ativoTesouro;
    Ativo ativoAcao;
    Endereco enderecoClienteNormal;
    Endereco enderecoClientePremium;
    Compra compraTeste;
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
                Conta.builder().saldo(BigDecimal.valueOf(10000.0)).carteira(new Carteira()).operacoes(new ArrayList<Operacao>()).build()
        );

        contaClientePremium = contaRepository.save(
                Conta.builder().saldo(BigDecimal.valueOf(500.0)).carteira(new Carteira()).operacoes(new ArrayList<Operacao>()).build()
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

        compraTeste = compraRepository.save(
                Compra.builder()
                        .ativo(ativoAcao)
                        .cliente(clientePremium)
                        .quantidade(2)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoAcao.getCotacao().multiply(BigDecimal.valueOf(2)))
                        .build()
        );

        compraService.disponibilizarCompra(compraTeste.getId(), administrador.getMatricula());
        compraService.confirmarCompra(clientePremium.getId(), clientePremium.getCodigo(), compraTeste.getId());
    }

    @AfterEach
    void tearDown() {
        ativoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Fluxo de solicitação de resgates pelo cliente")
    class FluxoSolicitacaoResgatePeloCliente {

        @Test
        @DisplayName("Solicitar resgate com cliente inexistente deve falhar")
        void solicitarResgateClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(post(uriResgates + "/99999/" + ativoAcao.getId())
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
        @DisplayName("Solicitar resgate com código de acesso incorreto deve falhar")
        void solicitarResgateCodigoInvalido() throws Exception {
            String responseJsonString = driver.perform(post(uriResgates + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
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
        @DisplayName("Solicitar resgate de quantidade maior que o que tem em carteira deve falhar")
        void solicitarResgateSaldoInsuficiente() throws Exception {
            String responseJsonString = driver.perform(post(uriResgates + "/" + clientePremium.getId() + "/" + ativoAcao.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("quantidade", "10"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals(
                    "Saldo insuficiente: cliente tentou resgatar 10 unidades desse ativo, mas possui apenas 2 na carteira.",
                    resultado.getMessage()
            );
        }

        @Test
        @DisplayName("Solicitar resgate de ativo que não existe na carteira do cliente deve falhar")
        void solicitarResgateAtivoNaoNaCarteira() throws Exception {
            String responseJsonString = driver.perform(post(uriResgates + "/" + clientePremium.getId() + "/" + ativoTesouro.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("quantidade", "1"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals("O cliente nao possui esse ativo em carteira!", resultado.getMessage());
        }
    }
}