package com.ufcg.psoft.commerce.service.extrato;


import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.OperacaoRepository;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;



@Service
public class ExtratoServiceImpl implements ExtratoService{

    private final OperacaoRepository operacaoRepository;
    private final ClienteService clienteService;

    @Autowired
    public ExtratoServiceImpl(OperacaoRepository operacaoRepository, ClienteService clienteService) {

        this.operacaoRepository = operacaoRepository;
        this.clienteService = clienteService;
    }

    @Transactional(readOnly = true)
    @Override
    public void gerarExtratoCSV(Long clienteId, String codigoAcesso, OutputStream outputStream) throws IOException {

        final String[] CSV_HEADER = {"Data", "Tipo Operacao", "Ativo", "Quantidade", "Valor Operacao", "Imposto Pago", "Valor Lucro"};

        Cliente cliente = clienteService.autenticar(clienteId,codigoAcesso);
        Conta conta = cliente.getConta();

        try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            if (conta == null) {
                try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(CSV_HEADER))) {
                    csvPrinter.flush();
                    return;
                }
            }

            List<Operacao> operacoes = operacaoRepository.findByClienteId(cliente.getId());

            try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(CSV_HEADER))) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                for (Operacao operacao : operacoes) {
                    String tipoOperacao = "";
                    String impostoPago = "N/A";
                    String valorLucro = "N/A";
                    String valorOperacao = "0.00";

                    if (operacao instanceof Compra) {
                        tipoOperacao = "COMPRA";
                    } else if (operacao instanceof Resgate) {
                        tipoOperacao = "RESGATE";
                        Resgate resgate = (Resgate) operacao;
                        impostoPago = resgate.getImposto() != null ? resgate.getImposto().toPlainString() : "0.00";
                        valorLucro = resgate.getLucro() != null ? resgate.getLucro().toPlainString() : "0.00";
                        valorOperacao = resgate.getValorResgatado().toPlainString() != null ? resgate.getValorResgatado().toPlainString() : "0.00";
                    }

                    csvPrinter.printRecord(
                            dateFormat.format(operacao.getDataSolicitacao()),
                            tipoOperacao,
                            operacao.getAtivo().getNome(),
                            operacao.getQuantidade(),
                            valorOperacao,
                            impostoPago,
                            valorLucro
                    );
                }
                csvPrinter.flush();
            }
        }
    }
}
