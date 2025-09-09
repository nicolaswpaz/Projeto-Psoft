package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import com.ufcg.psoft.commerce.service.autenticacao.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtratoServiceImpl implements ExtratoService {

    private final OperacaoRepository operacaoRepository;
    private final AutenticacaoService autenticacaoService;

    @Transactional(readOnly = true)
    @Override
    public void gerarExtratoCSV(Long idCliente, String codigoAcesso, OutputStream outputStream) throws IOException {
        final String[] csvHeader = {"Data", "Tipo da Operacao", "Ativo", "Quantidade", "Valor da Operacao", "Imposto Pago", "Valor Lucro", "Status da Operação"};

        Cliente cliente = autenticacaoService.autenticarCliente(idCliente, codigoAcesso);
        Conta conta = cliente.getConta();

        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(csvHeader).build())) {

            if (conta == null) {
                csvPrinter.flush();
                return;
            }

            List<Operacao> operacoes = operacaoRepository.findByClienteId(cliente.getId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Operacao operacao : operacoes) {
                csvPrinter.printRecord(gerarLinhaCSV(operacao, dateFormatter));
            }

            csvPrinter.flush();
        }
    }

    private Object[] gerarLinhaCSV(Operacao operacao, DateTimeFormatter dateFormatter) {
        String tipoOperacao;
        String valorOperacao = "0.00";
        String impostoPago = "N/A";
        String valorLucro = "N/A";
        String statusOperacao = "N/A";

        if (operacao instanceof Compra compra) {
            tipoOperacao = "COMPRA";
            valorOperacao = compra.getValorVenda() != null ? compra.getValorVenda().toPlainString() : "0.00";
            statusOperacao = compra.getStatusCompra() != null ? compra.getStatusCompra().name() : "N/A";
        } else if (operacao instanceof Resgate resgate) {
            tipoOperacao = "RESGATE";
            valorOperacao = resgate.getValorResgatado() != null ? resgate.getValorResgatado().toPlainString() : "0.00";
            impostoPago = resgate.getImposto() != null ? resgate.getImposto().toPlainString() : "0.00";
            valorLucro = resgate.getLucro() != null ? resgate.getLucro().toPlainString() : "0.00";
            statusOperacao = resgate.getStatusResgate() != null ? resgate.getStatusResgate().name() : "N/A";
        } else {
            tipoOperacao = "DESCONHECIDA";
        }

        return new Object[]{
                operacao.getDataSolicitacao().format(dateFormatter),
                tipoOperacao,
                operacao.getAtivo().getNome(),
                operacao.getQuantidade(),
                valorOperacao,
                impostoPago,
                valorLucro,
                statusOperacao
        };
    }
}


