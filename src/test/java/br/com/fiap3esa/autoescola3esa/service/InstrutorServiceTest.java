package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.domain.endereco.Endereco;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstrutorServiceTest {

    @Mock
    private InstrutorRepository repository;

    @InjectMocks
    private InstrutorService service;

    private final DadosEndereco dadosEndereco = new DadosEndereco(
            "Rua das Flores", "50", null, "Centro", "Osasco", "SP", "06010000");

    private Instrutor instrutorExistente() {
        Endereco endereco = new Endereco("Rua das Flores", "50", null, "Centro", "Osasco", "SP", "06010000");
        return new Instrutor(1L, "Carlos Lima", "carlos@email.com", "11988888888", "123456789",
                Especialidade.CARROS, endereco, true);
    }

    @Test
    void deveCadastrarInstrutor() {
        when(repository.save(any(Instrutor.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoInstrutor resultado = service.cadastrarInstrutor(new DadosCadastroInstrutor(
                "Carlos Lima", "carlos@email.com", "11988888888", "123456789", Especialidade.CARROS, dadosEndereco));

        assertThat(resultado.nome()).isEqualTo("Carlos Lima");
        assertThat(resultado.especialidade()).isEqualTo(Especialidade.CARROS);
        verify(repository).save(any(Instrutor.class));
    }

    @Test
    void deveListarApenasInstrutoresAtivos() {
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Instrutor> pagina = new PageImpl<>(List.of(instrutorExistente()), paginacao, 1);
        when(repository.findAllByAtivoTrue(paginacao)).thenReturn(pagina);

        Page<DadosListagemInstrutor> resultado = service.listarInstrutores(paginacao);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).email()).isEqualTo("carlos@email.com");
    }

    @Test
    void deveDetalharInstrutor() {
        when(repository.findById(1L)).thenReturn(Optional.of(instrutorExistente()));

        DadosDetalhamentoInstrutor resultado = service.detalharInstrutor(1L);

        assertThat(resultado.cnh()).isEqualTo("123456789");
    }

    @Test
    void deveLancarExcecaoAoDetalharInstrutorInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharInstrutor(99L))
                .isInstanceOf(InstrutorNotFoundException.class);
    }

    @Test
    void deveAtualizarInstrutor() {
        when(repository.findById(1L)).thenReturn(Optional.of(instrutorExistente()));
        when(repository.save(any(Instrutor.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoInstrutor resultado = service.atualizarInstrutor(
                new DadosAtualizacaoInstrutor(1L, null, null, null, Especialidade.VANS, null));

        assertThat(resultado.especialidade()).isEqualTo(Especialidade.VANS);
    }

    @Test
    void deveExcluirInstrutorLogicamente() {
        Instrutor instrutor = instrutorExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(instrutor));

        service.excluirInstrutor(1L);

        assertThat(instrutor.isAtivo()).isFalse();
        verify(repository).save(instrutor);
    }
}
