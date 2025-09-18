package com.ufcg.psoft.commerce.service.extrato;

import java.io.IOException;
import java.io.OutputStream;

public interface ExtratoService {

    void gerarExtratoCSV(Long clienteId, String codigoAcesso, OutputStream outputStream) throws IOException;
}
