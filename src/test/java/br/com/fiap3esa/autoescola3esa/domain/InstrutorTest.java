package br.com.fiap3esa.autoescola3esa.domain;

import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.DadosAtualizacaoInstrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.DadosCadastroInstrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Especialidade;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Instrutor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InstrutorTest {

    private final DadosEndereco endereco = new DadosEndereco(
            "Rua das Flores", "50", "Sala 2", "Centro", "Osasco", "SP", "06010000");

    private Instrutor novoInstrutor() {
        return new Instrutor(new DadosCadastroInstrutor(
                "Carlos Lima", "carlos@email.com", "11988888888", "123456789", Especialidade.CARROS, endereco));
    }

    @Test
    void deveCriarInstrutorAtivoAPartirDosDadosDeCadastro() {
        Instrutor instrutor = novoInstrutor();

        assertThat(instrutor.getNome()).isEqualTo("Carlos Lima");
        assertThat(instrutor.getEspecialidade()).isEqualTo(Especialidade.CARROS);
        assertThat(instrutor.isAtivo()).isTrue();
    }

    @Test
    void deveAtualizarEspecialidadeEEmailSemAlterarOsDemaisCampos() {
        Instrutor instrutor = novoInstrutor();

        instrutor.atualizarInformacoes(new DadosAtualizacaoInstrutor(
                1L, null, "novo@email.com", null, Especialidade.MOTOS, null));

        assertThat(instrutor.getEmail()).isEqualTo("novo@email.com");
        assertThat(instrutor.getEspecialidade()).isEqualTo(Especialidade.MOTOS);
        assertThat(instrutor.getNome()).isEqualTo("Carlos Lima");
    }

    @Test
    void exclusaoDeveSerLogica() {
        Instrutor instrutor = novoInstrutor();

        instrutor.excluir();

        assertThat(instrutor.isAtivo()).isFalse();
    }
}
