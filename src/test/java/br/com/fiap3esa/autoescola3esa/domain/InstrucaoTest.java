package br.com.fiap3esa.autoescola3esa.domain;

import br.com.fiap3esa.autoescola3esa.domain.agenda.Instrucao;
import br.com.fiap3esa.autoescola3esa.domain.agenda.MotivoCancelamento;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InstrucaoTest {

    @Test
    void cancelarDeveMarcarComoCanceladaEGuardarOMotivo() {
        Instrucao instrucao = new Instrucao(1L, null, null, LocalDateTime.now().plusDays(3), false, null);

        instrucao.cancelar(MotivoCancelamento.ALUNO_DESISTIU);

        assertThat(instrucao.isCancelada()).isTrue();
        assertThat(instrucao.getMotivoCancelamento()).isEqualTo(MotivoCancelamento.ALUNO_DESISTIU);
    }
}
