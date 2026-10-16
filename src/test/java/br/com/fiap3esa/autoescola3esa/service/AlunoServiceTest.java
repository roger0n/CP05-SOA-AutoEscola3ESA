package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.aluno.*;
import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.domain.endereco.Endereco;
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
class AlunoServiceTest {

    @Mock
    private AlunoRepository repository;

    @InjectMocks
    private AlunoService service;

    private final DadosEndereco dadosEndereco = new DadosEndereco(
            "Av. Paulista", "1106", null, "Bela Vista", "São Paulo", "SP", "01311000");

    private Aluno alunoExistente() {
        Endereco endereco = new Endereco("Av. Paulista", "1106", null, "Bela Vista", "São Paulo", "SP", "01311000");
        return new Aluno(1L, "Maria Silva", "maria@email.com", "11999999999", "12345678901", endereco, true);
    }

    @Test
    void deveCadastrarAluno() {
        when(repository.save(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoAluno resultado = service.cadastrarAluno(new DadosCadastroAluno(
                "Maria Silva", "maria@email.com", "11999999999", "12345678901", dadosEndereco));

        assertThat(resultado.nome()).isEqualTo("Maria Silva");
        assertThat(resultado.ativo()).isTrue();
        verify(repository).save(any(Aluno.class));
    }

    @Test
    void deveListarApenasAlunosAtivos() {
        Pageable paginacao = PageRequest.of(0, 10);
        Page<Aluno> pagina = new PageImpl<>(List.of(alunoExistente()), paginacao, 1);
        when(repository.findAllByAtivoTrue(paginacao)).thenReturn(pagina);

        Page<DadosListagemAluno> resultado = service.listarAlunos(paginacao);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo("Maria Silva");
    }

    @Test
    void deveDetalharAluno() {
        when(repository.findById(1L)).thenReturn(Optional.of(alunoExistente()));

        DadosDetalhamentoAluno resultado = service.detalharAluno(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.cpf()).isEqualTo("12345678901");
    }

    @Test
    void deveLancarExcecaoAoDetalharAlunoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.detalharAluno(99L))
                .isInstanceOf(AlunoNotFoundException.class);
    }

    @Test
    void deveAtualizarAluno() {
        when(repository.findById(1L)).thenReturn(Optional.of(alunoExistente()));
        when(repository.save(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoAluno resultado = service.atualizarAluno(
                new DadosAtualizacaoAluno(1L, "Maria Souza", null, null));

        assertThat(resultado.nome()).isEqualTo("Maria Souza");
    }

    @Test
    void deveExcluirAlunoLogicamente() {
        Aluno aluno = alunoExistente();
        when(repository.findById(1L)).thenReturn(Optional.of(aluno));

        service.excluirAluno(1L);

        assertThat(aluno.isAtivo()).isFalse();
        verify(repository).save(aluno);
    }
}
