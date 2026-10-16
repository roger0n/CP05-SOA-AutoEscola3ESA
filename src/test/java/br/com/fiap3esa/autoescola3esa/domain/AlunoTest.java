package br.com.fiap3esa.autoescola3esa.domain;

import br.com.fiap3esa.autoescola3esa.domain.aluno.Aluno;
import br.com.fiap3esa.autoescola3esa.domain.aluno.DadosAtualizacaoAluno;
import br.com.fiap3esa.autoescola3esa.domain.aluno.DadosCadastroAluno;
import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlunoTest {

    private final DadosEndereco endereco = new DadosEndereco(
            "Av. Paulista", "1106", null, "Bela Vista", "São Paulo", "SP", "01311000");

    private Aluno novoAluno() {
        return new Aluno(new DadosCadastroAluno(
                "Maria Silva", "maria@email.com", "11999999999", "12345678901", endereco));
    }

    @Test
    void deveCriarAlunoAtivoAPartirDosDadosDeCadastro() {
        Aluno aluno = novoAluno();

        assertThat(aluno.getNome()).isEqualTo("Maria Silva");
        assertThat(aluno.getCpf()).isEqualTo("12345678901");
        assertThat(aluno.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(aluno.isAtivo()).isTrue();
    }

    @Test
    void deveAtualizarApenasCamposInformados() {
        Aluno aluno = novoAluno();

        aluno.atualizarInformacoes(new DadosAtualizacaoAluno(1L, "Maria Souza", null, null));

        assertThat(aluno.getNome()).isEqualTo("Maria Souza");
        assertThat(aluno.getTelefone()).isEqualTo("11999999999");
        assertThat(aluno.getEndereco().getLogradouro()).isEqualTo("Av. Paulista");
    }

    @Test
    void exclusaoDeveSerLogica() {
        Aluno aluno = novoAluno();

        aluno.excluir();

        assertThat(aluno.isAtivo()).isFalse();
    }
}
