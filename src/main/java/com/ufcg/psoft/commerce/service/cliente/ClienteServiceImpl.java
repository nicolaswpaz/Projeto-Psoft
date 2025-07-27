{

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

        // Salva o cliente
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
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        if (!cliente.getCodigo().equals(codigoAcesso)) {
            throw new CodigoDeAcessoInvalidoException();
        }

        List<AtivoResponseDTO> ativosFiltrados = new ArrayList<>();
        List<AtivoResponseDTO> ativosDisponiveis = ativoService.listarAtivosDisponiveis();

        for(AtivoResponseDTO ativo : ativosDisponiveis) {
            if(cliente.getPlano() == TipoPlano.PREMIUM) {
                ativosFiltrados.add(ativo);
            } else {
                if(ativo.getTipo().name().equals("TESOURO_DIRETO")) {
                    ativosFiltrados.add(ativo);
                }
            }
        }

        return ativosFiltrados;
    }
}