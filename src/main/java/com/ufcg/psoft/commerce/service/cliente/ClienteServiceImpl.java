package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.exception.Cliente.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.Cliente.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.TesouroDireto;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.dto.Cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ufcg.psoft.commerce.repository.EnderecoRepository;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    EnderecoRepository enderecoRepository;
    @Autowired
    AdministradorService administradorService;
    @Autowired
    AtivoService ativoService;

    @Override
    public Cliente autenticar(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
        return cliente;
    }

    public Endereco salvarEnderecoSeNovo(Endereco endereco) {
        if (endereco != null && endereco.getId() == null) {
            return enderecoRepository.save(endereco);
        }
        return endereco;
    }

    @Override
    public ClienteResponseDTO criar(ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente cliente = modelMapper.map(clientePostPutRequestDTO, Cliente.class);

        clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public ClienteResponseDTO alterar(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente cliente = autenticar(id, codigoAcesso);

        modelMapper.map(clientePostPutRequestDTO, cliente);
        clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codigoAcesso) {
        Cliente cliente = autenticar(id, codigoAcesso);

        clienteRepository.delete(cliente);
    }

    @Override
    public ClienteResponseDTO recuperar(Long id, String codigoAcesso) {
        Cliente cliente = autenticar(id, codigoAcesso);
        return new ClienteResponseDTO(cliente);
    }

    @Override
    public List<ClienteResponseDTO> listarPorNome(String nome, String matriculaAdmin) {
        administradorService.autenticar(matriculaAdmin);

        List<Cliente> clientes = clienteRepository.findByNomeContaining(nome);
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponseDTO> listar(String matriculaAdmin) {
        administradorService.autenticar(matriculaAdmin);

        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<AtivoResponseDTO> listarAtivosDisponiveisPorPlano(Long idCliente, String codigoAcesso){
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        List<AtivoResponseDTO> ativosFiltrados = new ArrayList<>();
        List<AtivoResponseDTO> ativosDisponiveis = ativoService.listarAtivosDisponiveis();

        for(AtivoResponseDTO ativo : ativosDisponiveis){
            if(cliente.getPlano() == TipoPlano.PREMIUM){
                ativosFiltrados.add(ativo);
            }else{
                if(ativo.getTipo().getTipo().equals("tesouro")){
                    ativosFiltrados.add(ativo);
                }
            }
        }

        return ativosFiltrados;
    }
}
