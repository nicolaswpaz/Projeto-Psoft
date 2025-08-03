package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.Ativo.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.Endereco.EnderecoResponseDTO;
import com.ufcg.psoft.commerce.exception.Cliente.*;
import com.ufcg.psoft.commerce.model.Conta;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.enums.TipoPlano;
import com.ufcg.psoft.commerce.model.enums.TipoAtivo;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.dto.Cliente.ClientePostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.Cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.service.administrador.AdministradorService;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.conta.ContaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Autowired
    ContaService contaService;

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
        // Mapeia o DTO para a entidade Cliente
        Cliente cliente = modelMapper.map(clientePostPutRequestDTO, Cliente.class);

        // Processa o endereço
        if (clientePostPutRequestDTO.getEnderecoDTO() != null) {
            Endereco endereco = modelMapper.map(clientePostPutRequestDTO.getEnderecoDTO(), Endereco.class);
            endereco = enderecoRepository.save(endereco);
            cliente.setEndereco(endereco);
        }

        Conta novaConta = Conta.builder()
                .saldo("0.00") //valor inicial padrão
                .ativosDeInteresse(new ArrayList<>())
                .build();

        novaConta = contaService.salvar(novaConta);

        cliente.setConta(novaConta);

        cliente = clienteRepository.save(cliente);
        return modelMapper.map(cliente, ClienteResponseDTO.class);
    }


    @Override
    @Transactional
    public ClienteResponseDTO alterar(Long id, String codigoAcesso, ClientePostPutRequestDTO clientePostPutRequestDTO) {
        // Recupera o cliente existente
        Cliente cliente = autenticar(id, codigoAcesso);

        // Atualiza os campos básicos (exceto endereço)
        cliente.setNome(clientePostPutRequestDTO.getNome());
        cliente.setCpf(clientePostPutRequestDTO.getCpf());
        cliente.setCodigo(clientePostPutRequestDTO.getCodigo());
        cliente.setPlano(clientePostPutRequestDTO.getPlano());

        // Atualiza o endereço
        if (clientePostPutRequestDTO.getEnderecoDTO() != null) {
            atualizarEndereco(cliente, clientePostPutRequestDTO.getEnderecoDTO());
        }

        // Salva as alterações
        cliente = clienteRepository.save(cliente);
        return new ClienteResponseDTO(cliente);
    }


    private void atualizarEndereco(Cliente cliente, @NotNull(message = "Endereco obrigatorio") @Valid EnderecoResponseDTO enderecoDTO) {
        // Mantenha a referência original do endereço
        Endereco endereco = Optional.ofNullable(cliente.getEndereco())
                .orElseGet(Endereco::new);

        // Atualiza apenas campos não nulos (para não quebrar testes)
        if (enderecoDTO.getRua() != null) endereco.setRua(enderecoDTO.getRua());
        if (enderecoDTO.getBairro() != null) endereco.setBairro(enderecoDTO.getBairro());
        if (enderecoDTO.getNumero() != null) endereco.setNumero(enderecoDTO.getNumero());
        if (enderecoDTO.getCep() != null) endereco.setCep(enderecoDTO.getCep());
        if (enderecoDTO.getComplemento() != null) endereco.setComplemento(enderecoDTO.getComplemento());

        // Mantém a lógica original de persistência
        if (cliente.getEndereco() == null) {
            cliente.setEndereco(enderecoRepository.save(endereco));
        }
    }

    @Override
    @Transactional
    public void remover(Long id, String codigoAcesso) {
        Cliente cliente = autenticar(id, codigoAcesso);

        // Cria cópia da referência ao endereço
        Endereco endereco = cliente.getEndereco();

        // Remove primeiro o endereço se existir
        if (endereco != null) {
            enderecoRepository.delete(endereco);
        }

        // Atualiza o cliente para remover a referência
        cliente.setEndereco(null);
        clienteRepository.saveAndFlush(cliente);

        // Finalmente remove o cliente
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
    public List<AtivoResponseDTO> listarAtivosDisponiveisPorPlano(Long idCliente, String codigoAcesso) {
        Cliente cliente = autenticar(idCliente, codigoAcesso);

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
    public void marcarInteresseAtivoIndisponivel(Long idCliente, String codigoAcesso, Long idAtivo) {
        Cliente cliente = autenticar(idCliente, codigoAcesso);

        AtivoResponseDTO ativoResponseDTO= ativoService.recuperar(idAtivo);

        if(!ativoResponseDTO.isDisponivel()) {
            contaService.adicionarAtivoNaListaDeInteresse(cliente.getConta().getId(), ativoResponseDTO);
        }else{
            throw new AtivoDisponivelException();
        }
    }

    @Override
    public void marcarInteresseAtivoDisponivel(Long idCliente, String codigoAcesso, Long idAtivo) {
        Cliente cliente = autenticar(idCliente, codigoAcesso);

        if (cliente.getPlano() == TipoPlano.NORMAL) {
            throw new ClienteNaoPremiumException();
        }

        AtivoResponseDTO ativoResponseDTO= ativoService.recuperar(idAtivo);

        if (ativoResponseDTO.isDisponivel()){
            contaService.adicionarAtivoNaListaDeInteresse(cliente.getConta().getId(), ativoResponseDTO);
        }
    }

}