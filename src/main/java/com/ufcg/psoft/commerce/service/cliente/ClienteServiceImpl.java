package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.carteira.AtivoEmCarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.exception.ativo.AtivoDisponivelException;
import com.ufcg.psoft.commerce.exception.ativo.AtivoIndisponivelException;
import com.ufcg.psoft.commerce.exception.cliente.*;
import com.ufcg.psoft.commerce.exception.cliente.OperacaoInvalidaException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.model.enums.TipoInteresse;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.dto.cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.conta.ContaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ufcg.psoft.commerce.repository.EnderecoRepository;

@Service
public class ClienteServiceImpl implements ClienteService {


    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final EnderecoRepository enderecoRepository;
    private final AdministradorService administradorService;
    private final AtivoService ativoService;
    private final ContaService contaService;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              ModelMapper modelMapper,
                              EnderecoRepository enderecoRepository,
                              AdministradorService administradorService,
                              AtivoService ativoService,
                              ContaService contaService) {
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
        this.enderecoRepository = enderecoRepository;
        this.administradorService = administradorService;
        this.ativoService = ativoService;
        this.contaService = contaService;
    }

    @Override
    public Cliente autenticar(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }
        return cliente;
    }

    @Override
    @Transactional
    public ClienteResponseDTO criar(ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente cliente = modelMapper.map(clientePostPutRequestDTO, Cliente.class);

        Endereco endereco = modelMapper.map(clientePostPutRequestDTO.getEnderecoDTO(), Endereco.class);
        endereco = enderecoRepository.save(endereco);
        cliente.setEndereco(endereco);

        Conta novaConta = contaService.criarContaPadrao();
        cliente.setConta(novaConta);

        cliente = clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }


    @Override
    @Transactional
    public ClienteResponseDTO alterar(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO) {
        Cliente cliente = autenticar(id, codigoAcesso);

        cliente.setNome(clientePostPutRequestDTO.getNome());
        cliente.setCpf(clientePostPutRequestDTO.getCpf());
        cliente.setCodigo(clientePostPutRequestDTO.getCodigo());
        cliente.setPlano(clientePostPutRequestDTO.getPlano());

        if (clientePostPutRequestDTO.getEnderecoDTO() != null) {
            atualizarEndereco(cliente, clientePostPutRequestDTO.getEnderecoDTO());
        }

        cliente = clienteRepository.save(cliente);
        return new ClienteResponseDTO(cliente);
    }


    private void atualizarEndereco(Cliente cliente, EnderecoResponseDTO enderecoDTO) {
        Endereco endereco = Optional.ofNullable(cliente.getEndereco())
                .orElseGet(Endereco::new);

        if (enderecoDTO.getRua() != null) endereco.setRua(enderecoDTO.getRua());
        if (enderecoDTO.getBairro() != null) endereco.setBairro(enderecoDTO.getBairro());
        if (enderecoDTO.getNumero() != null) endereco.setNumero(enderecoDTO.getNumero());
        if (enderecoDTO.getCep() != null) endereco.setCep(enderecoDTO.getCep());
        if (enderecoDTO.getComplemento() != null) endereco.setComplemento(enderecoDTO.getComplemento());

        if (cliente.getEndereco() == null) {
            cliente.setEndereco(enderecoRepository.save(endereco));
        }
    }

    @Override
    @Transactional
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

        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    @Override
    public List<ClienteResponseDTO> listar(String matriculaAdmin) {
        administradorService.autenticar(matriculaAdmin);

        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    @Override
    public List<AtivoResponseDTO> listarAtivosDisponiveisPorPlano(Long id, String codigoAcesso) {
        Cliente cliente = autenticar(id, codigoAcesso);

        List<AtivoResponseDTO> ativosFiltrados = new ArrayList<>();
        List<AtivoResponseDTO> ativosDisponiveis = ativoService.listarAtivosDisponiveis();

        if (cliente.getPlano() == TipoPlano.PREMIUM) {
            return ativosDisponiveis;
        }

        for(AtivoResponseDTO ativo : ativosDisponiveis) {
            if(ativo.getTipo() == TipoAtivo.TESOURO_DIRETO) {
                ativosFiltrados.add(ativo);
            }
        }

        return ativosFiltrados;
    }

    @Override
    public void marcarInteresseAtivoIndisponivel(Long id, String codigoAcesso, Long idAtivo) {
        Cliente cliente = autenticar(id, codigoAcesso);

        Ativo ativo =  modelMapper.map(ativoService.recuperarDetalhado(idAtivo), Ativo.class);

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        if(!Boolean.TRUE.equals(ativo.isDisponivel())) {
            ativoService.registrarInteresse(cliente, ativo, TipoInteresse.DISPONIBILIDADE);
        }else{
            throw new AtivoDisponivelException();
        }
    }

    @Override
    public void marcarInteresseAtivoDisponivel(Long id, String codigoAcesso, Long idAtivo) {
        Cliente cliente = autenticar(id, codigoAcesso);

        if (cliente.getPlano() == TipoPlano.NORMAL) {
            throw new ClienteNaoPremiumException();
        }

        Ativo ativo =  modelMapper.map(ativoService.recuperarDetalhado(idAtivo), Ativo.class);

        if(ativo.getTipo() == TipoAtivo.TESOURO_DIRETO){
            throw new OperacaoInvalidaException();
        }

        if (Boolean.TRUE.equals(ativo.isDisponivel())){
            ativoService.registrarInteresse(cliente, ativo, TipoInteresse.VARIACAO_COTACAO);
        } else {
            throw new AtivoIndisponivelException();
        }
    }

    @Override
    public AtivoResponseDTO visualizarDetalhesAtivo(Long id, String codigoAcesso, Long idAtivo) {
        Cliente cliente = autenticar(id, codigoAcesso);

        AtivoResponseDTO ativo = ativoService.recuperarDetalhado(idAtivo);

        if (cliente.getPlano() == TipoPlano.NORMAL && ativo.getTipo() != TipoAtivo.TESOURO_DIRETO) {
            throw new ClienteNaoPremiumException();
        }

        return ativo;
    }

    @Override
    public void confirmarCompraAtivo(Long idCliente, Long idCompra, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        contaService.confirmarCompra(idCliente, idCompra);
    }

    @Override
    public List<AtivoEmCarteiraResponseDTO> visualizarCarteira(Long idCliente, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        return contaService.visualizarCarteira(idCliente);
    }
}