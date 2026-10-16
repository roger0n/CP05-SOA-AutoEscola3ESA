package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.agenda.*;
import br.com.fiap3esa.autoescola3esa.domain.agenda.validacao.ValidadorAgendamento;
import br.com.fiap3esa.autoescola3esa.domain.aluno.Aluno;
import br.com.fiap3esa.autoescola3esa.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esa.autoescola3esa.domain.aluno.AlunoRepository;
import br.com.fiap3esa.autoescola3esa.domain.endereco.Endereco;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Especialidade;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Instrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.InstrutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendaDeInstrucoesTest {

    @Mock
    private InstrucaoRepository repository;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private InstrutorRepository instrutorRepository;
    @Mock
    private ValidadorAgendamento validador;

    private AgendaDeInstrucoes agenda;

    private final Endereco endereco = new Endereco("Rua A", "1", null, "Centro", "Osasco", "SP", "06010000");
    private final Aluno aluno = new Aluno(1L, "Maria Silva", "maria@email.com", "11999999999", "12345678901", endereco, true);
    private final Instrutor instrutor = new Instrutor(2L, "Carlos Lima", "carlos@email.com", "11988888888",
            "123456789", Especialidade.CARROS, endereco, true);

    @BeforeEach
    void setUp() {
        agenda = new AgendaDeInstrucoes(repository, alunoRepository, instrutorRepository, List.of(validador));
    }

    private LocalDateTime dataFutura(int dias) {
        return LocalDateTime.now().plusDays(dias).withHour(10).withMinute(0).withSecond(0).withNano(0);
    }

    @Test
    void deveAgendarInstrucaoComInstrutorInformado() {
        LocalDateTime dataHora = dataFutura(3);
        DadosAgendamentoInstrucao dados = new DadosAgendamentoInstrucao(1L, 2L, null, dataHora);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(instrutorRepository.existsById(2L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrutorRepository.getReferenceById(2L)).thenReturn(instrutor);
        when(repository.save(any(Instrucao.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoAgendamento resultado = agenda.agendar(dados);

        assertThat(resultado.nomeAluno()).isEqualTo("Maria Silva");
        assertThat(resultado.nomeInstrutor()).isEqualTo("Carlos Lima");
        assertThat(resultado.dataHora()).isEqualTo(dataHora);
        assertThat(resultado.cancelada()).isFalse();
        verify(validador).validar(dados);
    }

    @Test
    void deveEscolherInstrutorDisponivelPelaEspecialidadeQuandoNaoInformado() {
        LocalDateTime dataHora = dataFutura(3);
        DadosAgendamentoInstrucao dados = new DadosAgendamentoInstrucao(1L, null, Especialidade.CARROS, dataHora);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(Especialidade.CARROS, dataHora))
                .thenReturn(instrutor);
        when(repository.save(any(Instrucao.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoAgendamento resultado = agenda.agendar(dados);

        assertThat(resultado.nomeInstrutor()).isEqualTo("Carlos Lima");
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoExiste() {
        DadosAgendamentoInstrucao dados = new DadosAgendamentoInstrucao(99L, 2L, null, dataFutura(3));
        when(alunoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> agenda.agendar(dados)).isInstanceOf(AlunoNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoQuandoNaoHaInstrutorNemEspecialidade() {
        DadosAgendamentoInstrucao dados = new DadosAgendamentoInstrucao(1L, null, null, dataFutura(3));
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);

        assertThatThrownBy(() -> agenda.agendar(dados)).isInstanceOf(ValidacaoException.class);
    }

    @Test
    void deveLancarExcecaoQuandoNenhumInstrutorEstaDisponivel() {
        LocalDateTime dataHora = dataFutura(3);
        DadosAgendamentoInstrucao dados = new DadosAgendamentoInstrucao(1L, null, Especialidade.MOTOS, dataHora);
        when(alunoRepository.existsById(1L)).thenReturn(true);
        when(alunoRepository.getReferenceById(1L)).thenReturn(aluno);
        when(instrutorRepository.escolherInstrutorAleatorioDisponivel(Especialidade.MOTOS, dataHora))
                .thenReturn(null);

        assertThatThrownBy(() -> agenda.agendar(dados))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("Nenhum instrutor disponível");
    }

    @Test
    void deveCancelarInstrucaoComAntecedenciaMinimaDe24Horas() {
        Instrucao instrucao = new Instrucao(10L, aluno, instrutor, LocalDateTime.now().plusDays(3), false, null);
        when(repository.findById(10L)).thenReturn(Optional.of(instrucao));
        when(repository.save(any(Instrucao.class))).thenAnswer(inv -> inv.getArgument(0));

        DadosDetalhamentoAgendamento resultado = agenda.cancelar(
                new DadosCancelamentoInstrucao(10L, MotivoCancelamento.ALUNO_DESISTIU));

        assertThat(resultado.cancelada()).isTrue();
        assertThat(resultado.motivoCancelamento()).isEqualTo(MotivoCancelamento.ALUNO_DESISTIU);
    }

    @Test
    void naoDeveCancelarComMenosDe24HorasDeAntecedencia() {
        Instrucao instrucao = new Instrucao(10L, aluno, instrutor, LocalDateTime.now().plusHours(5), false, null);
        when(repository.findById(10L)).thenReturn(Optional.of(instrucao));

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamentoInstrucao(10L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("24 horas");
    }

    @Test
    void naoDeveCancelarInstrucaoJaCancelada() {
        Instrucao instrucao = new Instrucao(10L, aluno, instrutor, LocalDateTime.now().plusDays(3), true,
                MotivoCancelamento.OUTROS);
        when(repository.findById(10L)).thenReturn(Optional.of(instrucao));

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamentoInstrucao(10L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(ValidacaoException.class)
                .hasMessageContaining("já está cancelada");
    }

    @Test
    void deveLancarExcecaoAoCancelarInstrucaoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agenda.cancelar(new DadosCancelamentoInstrucao(99L, MotivoCancelamento.OUTROS)))
                .isInstanceOf(InstrucaoNotFoundException.class);
    }
}
