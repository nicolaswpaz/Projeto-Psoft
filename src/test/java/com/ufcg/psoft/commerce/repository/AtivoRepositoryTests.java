/*package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.interfaces.TipoAtivo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Testes para AtivoRepository")
public class AtivoRepositoryTests {

    @Autowired
    AtivoRepository ativoRepository;

    Ativo ativo;

    @BeforeEach
    void setUp() {
        // Inicializa um objeto Ativo antes de cada teste
        ativo = Ativo.builder()
                .nome("Bitcoin")
                .cotacao("60000.00")
                .tipoAtivo(new TipoAtivo() {
                    //@Override
                    public String getNome() {
                        return "CriptoMoeda";
                    }
                })
                .descricao("Criptomoeda descentralizada")
                .disponivel(true)
                .build();
    }

    @Test
    @DisplayName("Deve salvar um ativo")
    void deveSalvarUmAtivo() {
        // Act
        Ativo ativoSalvo = ativoRepository.save(ativo);

        // Assert
        assertThat(ativoSalvo).isNotNull();
        assertThat(ativoSalvo.getId()).isNotNull();
        assertEquals("Bitcoin", ativoSalvo.getNome());
    }

    @Test
    @DisplayName("Deve buscar um ativo por ID")
    void deveBuscarAtivoPorId() {
        // Arrange
        ativoRepository.save(ativo);

        // Act
        Optional<Ativo> ativoEncontrado = ativoRepository.findById(ativo.getId());

        // Assert
        assertTrue(ativoEncontrado.isPresent());
        assertEquals("Bitcoin", ativoEncontrado.get().getNome());
    }

    @Test
    @DisplayName("Deve buscar ativos por nome contendo")
    void deveBuscarAtivosPorNomeContendo() {
        // Arrange
        Ativo ativo2 = Ativo.builder()
                .nome("Ethereum")
                .cotacao("3000.00")
                .tipoAtivo(new TipoAtivo() {
                    //@Override
                    public String getNome() {
                        return "CriptoMoeda";
                    }
                })
                .descricao("Plataforma descentralizada")
                .disponivel(true)
                .build();

        ativoRepository.save(ativo);
        ativoRepository.save(ativo2);

        // Act
        List<Ativo> ativosEncontrados = ativoRepository.findByNomeContaining("coin");

        // Assert
        assertFalse(ativosEncontrados.isEmpty());
        assertEquals(1, ativosEncontrados.size());
        assertEquals("Bitcoin", ativosEncontrados.get(0).getNome());
    }

    @Test
    @DisplayName("Deve atualizar um ativo")
    void deveAtualizarUmAtivo() {
        // Arrange
        ativoRepository.save(ativo);
        String novoNome = "Bitcoin Cash";
        ativo.setNome(novoNome);

        // Act
        Ativo ativoAtualizado = ativoRepository.save(ativo);

        // Assert
        assertThat(ativoAtualizado).isNotNull();
        assertEquals(novoNome, ativoAtualizado.getNome());
    }

    @Test
    @DisplayName("Deve deletar um ativo")
    void deveDeletarUmAtivo() {
        // Arrange
        ativoRepository.save(ativo);

        // Act
        ativoRepository.deleteById(ativo.getId());
        Optional<Ativo> ativoDeletado = ativoRepository.findById(ativo.getId());

        // Assert
        assertFalse(ativoDeletado.isPresent());
    }
}
*/