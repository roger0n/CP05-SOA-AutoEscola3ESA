package br.com.fiap3esa.autoescola3esa.domain.agenda.validacao;

import br.com.fiap3esa.autoescola3esa.domain.agenda.DadosAgendamentoInstrucao;
import br.com.fiap3esa.autoescola3esa.domain.agenda.InstrucaoRepository;
import br.com.fiap3esa.autoescola3esa.domain.agenda.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ValidadorLimiteDiarioAluno implements ValidadorAgendamento {
    private final InstrucaoRepository repository;

    @Override
    public void validar(DadosAgendamentoInstrucao dados) {
        LocalDateTime inicio = dados.dataHora().withHour(6).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = dados.dataHora().withHour(21 - 1).withMinute(59).withSecond(59).withNano(0);
        long quantidadeNoDia = repository.countByAlunoIdAndDataHoraBetweenAndCanceladaFalse(dados.idAluno(), inicio, fim);

        if (quantidadeNoDia >= 2) {
            throw new ValidacaoException("Permitido no máximo duas instruções por dia por aluno!");
        }
    }
}