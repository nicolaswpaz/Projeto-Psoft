package com.ufcg.psoft.commerce.controller;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.resgate.ResgateResponseDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.exception.cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.resgate.ResgateNaoExisteException;
import com.ufcg.psoft.commerce.exception.resgate.ResgateNaoPertenceAoClienteException;
import com.ufcg.psoft.commerce.listener.NotificacaoCompraDisponivel;
import com.ufcg.psoft.commerce.model.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    Ativo ativoTesouro2;
    Ativo ativoAcao;
    Ativo ativoCripto;
    Endereco enderecoClienteNormal;
    Endereco enderecoClientePremium;
    Compra compraAcao;
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
                Conta.builder().saldo(BigDecimal.valueOf(1000000.0)).carteira(new Carteira()).build()
        );

        contaClientePremium = contaRepository.save(
                Conta.builder().saldo(BigDecimal.valueOf(1000000.0)).carteira(new Carteira()).build()
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

        ativoTesouro2 = ativoRepository.save(Ativo.builder()
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

        ativoCripto = ativoRepository.save(
                Ativo.builder()
                        .nome("Cripto Teste")
                        .tipo(TipoAtivo.CRIPTOMOEDA)
                        .cotacao(BigDecimal.valueOf(1000.00))
                        .disponivel(true)
                        .descricao("Ativo CRIPTO")
                        .build()
        );

        compraAcao = compraRepository.save(
                Compra.builder()
                        .ativo(ativoAcao)
                        .cliente(clientePremium)
                        .quantidade(10)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoAcao.getCotacao().multiply(BigDecimal.valueOf(10)))
                        .build()
        );
        compraService.disponibilizarCompra(compraAcao.getId(), administrador.getMatricula());
        compraService.confirmarCompra(clientePremium.getId(), clientePremium.getCodigo(), compraAcao.getId());

        Compra compraTesouro = compraRepository.save(
                Compra.builder()
                        .ativo(ativoTesouro)
                        .cliente(clientePremium)
                        .quantidade(5)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoTesouro.getCotacao().multiply(BigDecimal.valueOf(5)))
                        .build()
        );
        compraService.disponibilizarCompra(compraTesouro.getId(), administrador.getMatricula());
        compraService.confirmarCompra(clientePremium.getId(), clientePremium.getCodigo(), compraTesouro.getId());

        Compra compraCripto = compraRepository.save(
                Compra.builder()
                        .ativo(ativoCripto)
                        .cliente(clientePremium)
                        .quantidade(2)
                        .dataSolicitacao(LocalDate.now())
                        .valorVenda(ativoCripto.getCotacao().multiply(BigDecimal.valueOf(2)))
                        .build()
        );
        compraService.disponibilizarCompra(compraCripto.getId(), administrador.getMatricula());
        compraService.confirmarCompra(clientePremium.getId(), clientePremium.getCodigo(), compraCripto.getId());
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
                            .param("quantidade", "11"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);
            assertEquals(
                    "Saldo insuficiente: cliente tentou resgatar 11 unidades desse ativo, mas possui apenas 10 na carteira.",
                    resultado.getMessage()
            );
        }

        @Test
        @DisplayName("Solicitar resgate de ativo que não existe na carteira do cliente deve falhar")
        void solicitarResgateAtivoNaoNaCarteira() throws Exception {
            String responseJsonString = driver.perform(post(uriResgates + "/" + clientePremium.getId() + "/" + ativoTesouro2.getId())
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

    @Nested
    @DisplayName("Teste do imposto no resgate sobre lucro")
    class ResgateImposto {
        @Test
        @DisplayName("Resgate total deve remover ativo da carteira")
        void resgateTotalRemoveAtivoDaCarteira() throws Exception {

            ativoAcao.setCotacao(BigDecimal.valueOf(55.00));
            ativoRepository.save(ativoAcao);


            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 10);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal lucroUnitario = BigDecimal.valueOf(15.00).subtract(BigDecimal.valueOf(10.00)); // 5,00
            BigDecimal lucroTotal = lucroUnitario.multiply(BigDecimal.valueOf(10)); // 25,00
            BigDecimal impostoEsperado = lucroTotal.multiply(BigDecimal.valueOf(0.15)); // 3,75

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Imposto deve ser R$3,75");

            boolean ativoNaCarteira = clientePremium.getConta()
                    .getCarteira()
                    .getAtivosEmCarteira()
                    .stream()
                    .anyMatch(ca -> ca.getAtivo().getId().equals(ativoAcao.getId()));

            assertFalse(ativoNaCarteira, "Ativo deve ser removido da carteira após resgate total");
        }

        @Test
        @DisplayName("Resgate parcial deve calcular imposto corretamente e manter ativo na carteira")
        void resgateParcialDeveCalcularImpostoEManterCarteira() throws Exception {
            ativoAcao.setCotacao(BigDecimal.valueOf(70.0));
            ativoRepository.save(ativoAcao);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 4);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal lucroUnitario = BigDecimal.valueOf(70.0).subtract(BigDecimal.valueOf(50.0)); // 20
            BigDecimal lucroTotal = lucroUnitario.multiply(BigDecimal.valueOf(4)); // 80
            BigDecimal impostoEsperado = lucroTotal.multiply(BigDecimal.valueOf(0.15)); // 12

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()));

            long quantidadeRestante = clientePremium.getConta()
                    .getCarteira()
                    .getAtivosEmCarteira()
                    .stream()
                    .filter(ca -> ca.getAtivo().getId().equals(ativoAcao.getId()))
                    .findFirst()
                    .get()
                    .getQuantidade();

            assertEquals(6, quantidadeRestante);
        }

        @Test
        @DisplayName("Arredondamento de imposto deve seguir regra do sistema")
        void arredondamentoImposto() throws Exception {

            ativoAcao.setCotacao(BigDecimal.valueOf(56.7));
            ativoRepository.save(ativoAcao);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal impostoEsperado = BigDecimal.valueOf(1.01);

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Imposto deve ser arredondado para 1,00");
        }

        @Test
        @DisplayName("Resgate com ativo em desvalorização não deve cobrar imposto")
        void resgateAtivoDesvalorizado() throws Exception {

            ativoAcao.setCotacao(BigDecimal.valueOf(20));
            ativoRepository.save(ativoAcao);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal impostoEsperado = BigDecimal.ZERO;
            BigDecimal valorResgatadoEsperado = BigDecimal.valueOf(20.0);

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Imposto deve ser zero para lucro negativo");
            assertEquals(0, valorResgatadoEsperado.compareTo(resgateConfirmado.getValorResgatado()),
                    "Valor resgatado deve ser 20,00");
        }

        @Test
        @DisplayName("Resgate parcial deve calcular imposto corretamente e manter ativo na carteira")
        void resgateParcialCalculaImpostoEManterAtivo() throws Exception {
            ativoAcao.setCotacao(BigDecimal.valueOf(70.0));
            ativoRepository.save(ativoAcao);


            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 4);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal lucroUnitario = BigDecimal.valueOf(60.00).subtract(BigDecimal.valueOf(40.00)); //20,00
            BigDecimal lucroTotal = lucroUnitario.multiply(BigDecimal.valueOf(4)); //80,00
            BigDecimal impostoEsperado = lucroTotal.multiply(BigDecimal.valueOf(0.15)); //12,00

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Imposto deve ser R$12,00");

            // Verificar que ainda restam 6 unidades na carteira
            long quantidadeRestante = clientePremium.getConta()
                    .getCarteira()
                    .getAtivosEmCarteira()
                    .stream()
                    .filter(ca -> ca.getAtivo().getId().equals(ativoAcao.getId()))
                    .findFirst()
                    .get()
                    .getQuantidade();

            assertEquals(6, quantidadeRestante, "Deveriam restar 6 unidades na carteira");
        }

        @Test
        @DisplayName("Resgate de Tesouro Direto deve aplicar 10% de imposto")
        void resgateTesouroDiretoAplica10Porcento() throws Exception {
            ativoTesouro.setTipo(TipoAtivo.TESOURO_DIRETO);
            ativoTesouro.setCotacao(BigDecimal.valueOf(110.00));
            ativoRepository.save(ativoTesouro);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoTesouro.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());
            BigDecimal impostoEsperado = new BigDecimal("1.00");

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Tesouro Direto deve aplicar 10% de imposto");
        }

        @Test
        @DisplayName("Criptomoeda com lucro até R$5.000 deve aplicar 15%")
        void criptomoedaAte5000Aplica15Porcento() throws Exception {
            ativoCripto.setCotacao(BigDecimal.valueOf(5999.00));
            ativoRepository.save(ativoCripto);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoCripto.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal impostoEsperado = new BigDecimal("749.85");

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Criptomoeda com lucro até R$5.000 deve aplicar 15%");
        }

        @Test
        @DisplayName("Criptomoeda com lucro acima de R$5.000 deve aplicar 22.5%")
        void criptomoedaAcima5000Aplica225Porcento() throws Exception {
            ativoCripto.setCotacao(BigDecimal.valueOf(6001.00));
            ativoRepository.save(ativoCripto);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoCripto.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal impostoEsperado = new BigDecimal("1125.23");

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Criptomoeda com lucro acima de R$5.000 deve aplicar 22,5%");
        }

        @Test
        @DisplayName("Criptomoeda no limite exato de R$5.000 deve aplicar 15%")
        void criptomoedaNoLimite5000Aplica15Porcento() throws Exception {
            ativoCripto.setCotacao(BigDecimal.valueOf(6000.00));
            ativoRepository.save(ativoCripto);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoCripto.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            // Lucro: 6000,00 - 1000,00 = 5000,00
            // Imposto: 5000,00 * 0,15 = 750,00
            BigDecimal impostoEsperado = new BigDecimal("750.00");

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Criptomoeda no limite de R$5.000 deve aplicar 15%");
        }

        @Test
        @DisplayName("Criptomoeda em desvalorização não deve cobrar imposto")
        void criptomoedaDesvalorizada() throws Exception {
            ativoCripto.setCotacao(BigDecimal.valueOf(800.00));
            ativoRepository.save(ativoCripto);

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoCripto.getId(), 1);

            ResgateResponseDTO resgateConfirmado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            BigDecimal impostoEsperado = BigDecimal.ZERO;

            assertEquals(0, impostoEsperado.compareTo(resgateConfirmado.getImposto()),
                    "Criptomoeda em desvalorização não deve cobrar imposto");
        }
    }

    @Nested
    @DisplayName("Consulta/Acompanhamento do resgate")
    class ConsultaResgate {

        @Test
        @DisplayName("Resgate solicitado deve conter data, imposto inicial e estado SOLICITADO")
        void resgateSolicitadoDeveConterDataEImposto() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO consultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            assertNotNull(consultado.getDataSolicitacao(), "Data do resgate deve ser registrada");
            assertNotNull(consultado.getImposto(), "Imposto deve estar presente (mesmo que zero)");
            assertEquals(StatusResgate.SOLICITADO, consultado.getStatusResgate(),
                    "Status inicial deve ser SOLICITADO");
        }

        @Test
        @DisplayName("Resgate confirmado deve conter data, imposto e estado EM_CONTA")
        void resgateConfirmadoDeveConterDataEImposto() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);
            resgateService.confirmarResgate(resgateSolicitado.getId(), administrador.getMatricula());

            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO consultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            assertNotNull(consultado.getDataSolicitacao(), "Data do resgate deve permanecer registrada");
            assertNotNull(consultado.getImposto(), "Imposto deve ser calculado e registrado");
            assertEquals(StatusResgate.EM_CONTA, consultado.getStatusResgate(),
                    "Após liquidação, status deve ser EM_CONTA");
        }

        @Test
        @DisplayName("Cliente deve conseguir consultar um resgate solicitado")
        void clienteConsultaResgateSolicitado() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);
            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO resgateConsultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            assertEquals(StatusResgate.SOLICITADO, resgateConsultado.getStatusResgate());
            assertNotNull(resgateConsultado.getDataSolicitacao());
            assertEquals(clientePremium.getId(), resgateConsultado.getCliente().getId());
        }

        @Test
        @DisplayName("Cliente deve conseguir consultar um resgate liquidado (Em conta)")
        void clienteConsultaResgateLiquidado() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            resgateService.confirmarResgate(resgateSolicitado.getId(), administrador.getMatricula());

            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO resgateConsultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            assertEquals(StatusResgate.EM_CONTA, resgateConsultado.getStatusResgate());
            assertNotNull(resgateConsultado.getImposto());
            assertNotNull(resgateConsultado.getDataSolicitacao());
        }

        @Test
        @DisplayName("Cliente não pode consultar resgate que não é dele")
        void clienteNaoPodeConsultarResgateDeOutroCliente() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            driver.perform(get(uriResgates + "/" + clienteNormal.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clienteNormal.getCodigo()))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertInstanceOf(
                            ResgateNaoPertenceAoClienteException.class,
                            result.getResolvedException()
                    ));
        }

        @Test
        @DisplayName("Cliente não existente não pode consultar resgate")
        void clienteNaoExistenteNaoPodeConsultarResgate() throws Exception {
            driver.perform(get(uriResgates + "/99999/1")
                            .param("codigoAcesso", "1234"))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertInstanceOf(ClienteNaoExisteException.class, result.getResolvedException()));
        }

        @Test
        @DisplayName("Cliente com código de acesso inválido não pode consultar resgate")
        void clienteComCodigoInvalidoNaoPodeConsultarResgate() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", "senhaErrada"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Consulta de resgate liquidado deve exibir imposto e valor resgatado corretamente")
        void consultaResgateLiquidadoExibeImpostoEValor() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            ResgateResponseDTO resgateLiquidado = resgateService.confirmarResgate(
                    resgateSolicitado.getId(), administrador.getMatricula());

            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateLiquidado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO consultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            assertEquals(StatusResgate.EM_CONTA, consultado.getStatusResgate());
            assertNotNull(consultado.getImposto(), "Imposto não deve ser nulo");
            assertTrue(consultado.getValorResgatado().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Data de solicitação deve ser preenchida corretamente")
        void dataDeSolicitacaoPreenchida() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO consultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            assertNotNull(consultado.getDataSolicitacao());
            assertEquals(LocalDate.now(), consultado.getDataSolicitacao());
        }

        @Test
        @DisplayName("Consultar resgate inexistente deve lançar ResgateNaoExisteException")
        void consultaResgateInexistente() throws Exception {
            driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/99999")
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertInstanceOf(
                            ResgateNaoExisteException.class,
                            result.getResolvedException()
                    ));
        }

        @Test
        @DisplayName("Resgate liquidado deve atualizar o saldo da conta do cliente")
        void resgateAtualizaSaldoConta() throws Exception {
            BigDecimal saldoInicial = clientePremium.getConta().getSaldo();

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            resgateService.confirmarResgate(resgateSolicitado.getId(), administrador.getMatricula());

            String responseJsonString = driver.perform(get(uriResgates + "/" + clientePremium.getId() + "/" + resgateSolicitado.getId())
                            .param("codigoAcesso", clientePremium.getCodigo()))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO consultado = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);

            BigDecimal saldoFinal = clientePremium.getConta().getSaldo();
            assertTrue(saldoFinal.compareTo(saldoInicial) > 0, "Saldo deve aumentar após resgate");
            assertEquals(StatusResgate.EM_CONTA, consultado.getStatusResgate());
        }

        @Test
        @DisplayName("Resgate liquidado deve remover apenas o ativo da carteira que zerar")
        void resgateRemoveApenasAtivoQueZerar() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 10);

            resgateService.confirmarResgate(resgateSolicitado.getId(), administrador.getMatricula());

            boolean ativoAindaNaCarteira = clientePremium.getConta()
                    .getCarteira()
                    .getAtivosEmCarteira()
                    .stream()
                    .anyMatch(ativo -> ativo.getAtivo().getId().equals(ativoAcao.getId()));

            assertFalse(ativoAindaNaCarteira, "O ativo resgatado deve ser removido se a quantidade chegar a zero");
        }
    }

    @Nested
    @DisplayName("Confirmar resgate pelo Admin, caso de sucesso")
    class ConfirmaResgatePeloAdmin{

        @Test
        @DisplayName("Caso de sucesso para quando o ADMIN confirma o resgate do cliente")
        void resgateConfirmadoComSucesso() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);

            String responseJsonString = driver.perform(put(uriResgates + "/admin/" + resgateSolicitado.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ResgateResponseDTO resgateConfirmadoDTO = objectMapper.readValue(responseJsonString, ResgateResponseDTO.class);
            assertNotNull(resgateConfirmadoDTO);
            assertEquals(resgateSolicitado.getId(), resgateConfirmadoDTO.getId());
            assertEquals("EM_CONTA", resgateConfirmadoDTO.getStatusResgate().name());
        }

        @Test
        @DisplayName("Quando o Administrador tenta confirmar um resgate inexistente")
        void quandoConfirmamosUmResgateInesistente() throws Exception{

            long idResgateInexistente = 9999L;

            String respondeJsonString = driver.perform(put(uriResgates + "/admin/" + idResgateInexistente + "/confirmar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(respondeJsonString, CustomErrorType.class);
            assertEquals("O resgate nao existe!", resultado.getMessage());

        }
        @Test
        @DisplayName("Quando tentamos confirmar um Resgate já confirmado anteriormente")
        void quandoConfirmamosUmResgateJaConfirmado() throws Exception {

            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);
            resgateService.confirmarResgate(resgateSolicitado.getId(), administrador.getMatricula());

            driver.perform(put(uriResgates + "/admin/" + resgateSolicitado.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", administrador.getMatricula()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Status de compra nao permite essa acao"));
        }

        @Test
        @DisplayName("Quando tentamos confirmar um resgate com uma matrícula inválida")
        void matriculaInvalidaAdmin() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);
            String matriculaInvalida = "99999";

            driver.perform(put(uriResgates + "/admin/" + resgateSolicitado.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("matriculaAdmin", matriculaInvalida))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Autenticacao falhou!"));

        }

        @Test
        @DisplayName("Deve retornar Bad Request ao tentar confirmar sem matrícula do admin")
        void quandoNaoInformamosAMatriculaDoAdministrador() throws Exception {
            ResgateResponseDTO resgateSolicitado = resgateService.solicitarResgate(
                    clientePremium.getId(), clientePremium.getCodigo(), ativoAcao.getId(), 1);
            driver.perform(put(uriResgates + "/admin/" + resgateSolicitado.getId() + "/confirmar")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }




}