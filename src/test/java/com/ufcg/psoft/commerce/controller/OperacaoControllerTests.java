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
import com.ufcg.psoft.commerce.service.extrato.ExtratoService;
import com.ufcg.psoft.commerce.service.operacao.compra.CompraService;
import com.ufcg.psoft.commerce.service.operacao.resgate.ResgateService;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Testes do controlador de Operações")
class OperacaoControllerTests {

    final String uriOperacoes = "/operacoes";

    @Autowired
    MockMvc driver;

    @MockBean
    private ExtratoService extratoService;

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
    Compra compraTeste3;
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
                Conta.builder().saldo(BigDecimal.valueOf(10000.0)).carteira(new Carteira()).build()
        );

        contaClientePremium = contaRepository.save(
                Conta.builder().saldo(BigDecimal.valueOf(50000.0)).carteira(new Carteira()).build()
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

        compraTeste3 = compraRepository.save(
                Compra.builder()
                        .ativo(ativoTesouro)
                        .cliente(clienteNormal)
                        .quantidade(3)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoTesouro.getCotacao().multiply(BigDecimal.valueOf(10)))
                        .build()
        );

        // Compra do Cliente normal
        compraService.disponibilizarCompra(compraTeste3.getId(), administrador.getMatricula());
        compraService.confirmarCompra(clienteNormal.getId(), clienteNormal.getCodigo(), compraTeste3.getId());

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
        @DisplayName("A consulta das operações (COMPRA) do cliente com os dados corretos deve funcionar")
        void consultarOperacoesDoClienteComDadosCorretosCompra() throws Exception {
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

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            assertEquals(1, operacoes.size());
            assertEquals("COMPRA", operacoes.get(0).getTipoOperacao());

            Compra compra = compraRepository.findById(operacoes.get(0).getId()).orElseThrow();
            assertEquals(StatusCompra.EM_CARTEIRA, compra.getStatusCompra());
        }

        @Test
        @DisplayName("A consulta das operações (RESGATE) do cliente com os dados corretos deve funcionar")
        void consultarOperacoesDoClienteComDadosCorretosResgate() throws Exception {
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

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            assertEquals(1, operacoes.size());
            assertEquals("RESGATE", operacoes.get(0).getTipoOperacao());

            Resgate resgate = resgateRepository.findById(operacoes.get(0).getId()).orElseThrow();
            assertEquals(StatusResgate.EM_CONTA, resgate.getStatusResgate());
        }
    }

    @Nested
    @DisplayName("Fluxo de consulta de operações pelo Administrador")
    class FluxoAdministradorConsultaOperacaoCompraClientes {

        @Test
        @DisplayName("Consulta todas as operações de todos os clientes")
        void adminConsultaOperacoesDeTodosClientes() throws Exception {
            String responseJsonString = driver.perform(get(uriOperacoes + "/admin")
                            .param("matriculaAdmin", administrador.getMatricula())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            assertEquals(4, operacoes.size());

            assertEquals(
                    List.of("COMPRA", "COMPRA", "COMPRA", "RESGATE"),
                    operacoes.stream().map(OperacaoResponseDTO::getTipoOperacao).toList()
            );
        }

        @Test
        @DisplayName("Consulta todas as operações de um tipo especifico")
        void adminConsultaOperacoesTipoEspecifico() throws Exception {
            String responseJsonString = driver.perform(get(uriOperacoes + "/admin")
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("tipoOperacao", "COMPRA")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });
            assertEquals(3, operacoes.size());

            assertTrue(
                    operacoes.stream().allMatch(op -> "COMPRA".equals(op.getTipoOperacao()))
            );

            assertEquals(operacoes.get(0).getId(), compraTeste1.getId());
        }

        @Test
        @DisplayName("Consulta operações de um cliente especifico")
        void adminConsultaOperacaoClienteEspecifico() throws Exception {
            String responseJsonString = driver.perform(get(uriOperacoes + "/admin")
                            .param("matriculaAdmin", administrador.getMatricula())
                            .param("idCliente", String.valueOf(clientePremium.getId()))
                            .param("data", LocalDate.now().toString())
                            .param("tipoOperacao", "RESGATE")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<OperacaoResponseDTO> operacoes = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertEquals(1, operacoes.size());
            assertEquals("RESGATE", operacoes.get(0).getTipoOperacao());
            assertEquals(resgateTeste.getId(), operacoes.get(0).getId());
        }
    }

    @Nested
    @DisplayName("Fluxo de consulta do extratato gerado pelo cliente")
    class clienteConsultaExtrato {
        @Test
        @DisplayName("Deve exportar extrato CSV com sucesso")
        void exportarExtratoCSV_sucesso() throws Exception {
            Long idCliente = clientePremium.getId();
            String codigoAcesso = clientePremium.getCodigo();

            doAnswer(invocation -> {
                OutputStream os = invocation.getArgument(2);
                os.write("Data,Tipo da Operacao,Ativo\n01/01/2025,COMPRA,TESTE\n".getBytes());
                return null;
            }).when(extratoService).gerarExtratoCSV(eq(idCliente), eq(codigoAcesso), any());

            MvcResult result = driver.perform(get("/operacoes/clientes/{idCliente}/gerarExtrato", idCliente)
                            .param("codigoAcesso", codigoAcesso))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, Matchers.containsString("attachment; filename=")))
                    .andExpect(content().contentType("text/csv"))
                    .andReturn();

            String csvContent = result.getResponse().getContentAsString();
            assertThat(csvContent)
                    .contains("Data,Tipo da Operacao,Ativo")
                    .contains("COMPRA");
        }

        @Test
        @DisplayName("Deve exportar extrato CSV vazio quando não há operações")
        void exportarExtratoCSV_vazio() throws Exception {
            Long idCliente = 1L;
            String codigoAcesso = "123456";

            doAnswer(invocation -> {
                OutputStream os = invocation.getArgument(2);
                Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                CSVPrinter csvPrinter = CSVFormat.DEFAULT.builder()
                        .setHeader("Data", "Tipo da Operacao", "Ativo", "Quantidade",
                                "Valor da Operacao", "Imposto Pago", "Valor Lucro", "Status da Operação")
                        .build()
                        .print(writer);
                csvPrinter.flush();
                csvPrinter.close();
                return null;
            }).when(extratoService).gerarExtratoCSV(eq(idCliente), eq(codigoAcesso), any());

            MvcResult result = driver.perform(get("/operacoes/clientes/{idCliente}/gerarExtrato", idCliente)
                            .param("codigoAcesso", codigoAcesso))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, Matchers.containsString("attachment; filename=")))
                    .andExpect(content().contentTypeCompatibleWith("text/csv"))
                    .andReturn();

            byte[] responseBytes = result.getResponse().getContentAsByteArray();
            String csvContent = new String(responseBytes, StandardCharsets.UTF_8);

            assertThat(csvContent)
                    .contains("Data,Tipo da Operacao,Ativo,Quantidade,Valor da Operacao,Imposto Pago,Valor Lucro,Status da Operação");

            String[] lines = csvContent.split("\\r?\\n");
            assertThat(lines).hasSize(1);
        }
    }
}
