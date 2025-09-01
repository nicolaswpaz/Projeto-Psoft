package com.ufcg.psoft.commerce.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.operacao.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.listener.NotificacaoCompraDisponivel;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.StatusCompra;
import com.ufcg.psoft.commerce.model.enums.StatusResgate;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes do controlador de Operações")
class OperacaoControllerTests {

    final String uriOperacoes = "/operacoes";

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
    ResgateRepository resgateRepository;

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
    Compra compraTeste1;
    Compra compraTeste2;
    Resgate resgateTeste;
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
                Conta.builder().saldo(BigDecimal.valueOf(50000.0)).carteira(new Carteira()).operacoes(new ArrayList<Operacao>()).build()
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

        compraTeste1 = compraRepository.save(
                Compra.builder()
                        .ativo(ativoAcao)
                        .cliente(clientePremium)
                        .quantidade(2)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoAcao.getCotacao().multiply(BigDecimal.valueOf(2)))
                        .build()
        );

        compraService.disponibilizarCompra(compraTeste1.getId(), administrador.getMatricula());

        // Status EM_CARTEIRA
        compraService.confirmarCompra(clientePremium.getId(), clientePremium.getCodigo(), compraTeste1.getId());

        compraTeste2 = compraRepository.save(
                Compra.builder()
                        .ativo(ativoTesouro)
                        .cliente(clientePremium)
                        .quantidade(10)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoTesouro.getCotacao().multiply(BigDecimal.valueOf(10)))
                        .build()
        );

        // Status DISPONIVEL
        compraService.disponibilizarCompra(compraTeste2.getId(), administrador.getMatricula());


        resgateTeste = resgateRepository.save(
                Resgate.builder()
                        .ativo(ativoAcao)
                        .cliente(clientePremium)
                        .quantidade(1)
                        .dataSolicitacao(LocalDate.now())
                        .statusResgate(StatusResgate.SOLICITADO)
                        .valorResgatado(ativoAcao.getCotacao().multiply(BigDecimal.valueOf(1)))
                        .lucro(BigDecimal.ZERO)
                        .imposto(BigDecimal.ZERO)
                        .build()
        );

        // Status EM_CONTA
        resgateService.confirmarResgate(resgateTeste.getId(), administrador.getMatricula());
    }

    @AfterEach
    void tearDown() {
        ativoRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    @Nested
    @DisplayName("Fluxo de consulta de operações pelo CLIENTE")
    class FluxoConsultaOperacoesCliente {

        @Test
        @DisplayName("A consulta das operações (COMPRA) do cliente com os dados corretos deve funcionart")
        void consultarOperacoesDoClienteComDadopsCorretosCompra() throws Exception {
            String responseJsonString = driver.perform(get(uriOperacoes + "/" + clientePremium.getId())
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("tipoAtivo", "ACAO")
                            .param("dataInicio", LocalDate.now().toString())
                            .param("dataFim", LocalDate.now().toString())
                            .param("statusOperacao", "EM_CARTEIRA")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {});
            assertEquals(1, operacoes.size());
            assertEquals("COMPRA", operacoes.get(0).getTipoOperacao());

            Compra compra = compraRepository.findById(operacoes.get(0).getId()).orElseThrow();
            assertEquals(StatusCompra.EM_CARTEIRA, compra.getStatusCompra());
        }

        @Test
        @DisplayName("A consulta das operações (RESGATE) do cliente com os dados corretos deve funcionar")
        void consultarOperacoesDoClienteComDadopsCorretosResgate() throws Exception {
            String responseJsonString = driver.perform(get(uriOperacoes + "/" + clientePremium.getId())
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .param("tipoAtivo", "ACAO")
                            .param("dataInicio", LocalDate.now().toString())
                            .param("dataFim", LocalDate.now().toString())
                            .param("statusOperacao", "EM_CONTA")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {});
            assertEquals(1, operacoes.size());
            assertEquals("RESGATE", operacoes.get(0).getTipoOperacao());

            Resgate resgate = resgateRepository.findById(operacoes.get(0).getId()).orElseThrow();
            assertEquals(StatusResgate.EM_CONTA, resgate.getStatusResgate());
        }
    }
}