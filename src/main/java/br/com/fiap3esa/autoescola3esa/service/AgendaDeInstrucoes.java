package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.agenda.*;
import br.com.fiap3esa.autoescola3esa.domain.agenda.validacao.ValidadorAgendamento;
import br.com.fiap3esa.autoescola3esa.domain.aluno.Aluno;
import br.com.fiap3esa.autoescola3esa.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esa.autoescola3esa.domain.aluno.AlunoRepository;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Instrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.InstrutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaDeInstrucoes {
    public final InstrucaoRepository repository;
    public final AlunoRepository alunoRepository;
    public final InstrutorRepository instrutorRepository;
    public final List<ValidadorAgendamento> validadoresAgendamento;

    @Transactional
    public DadosDetalhamentoAgendamento agendar(DadosAgendamentoInstrucao dados) {
        if (!alunoRepository.existsById(dados.idAluno())) {
            throw new AlunoNotFoundException("ID do aluno informado não existe!");
        }
        if (dados.idInstrutor() != null && !instrutorRepository.existsById(dados.idInstrutor())) {
            throw new InstrutorNotFoundException("ID do instrutor informado não existe!");
        }
        //Validações
        validadoresAgendamento.forEach(validador -> validador.validar(dados));

        Aluno aluno = alunoRepository.getReferenceById(dados.idAluno());
        Instrutor instrutor = escolherInstrutor(dados);
        if (instrutor == null) {
            throw new ValidacaoException("Nenhum instrutor disponível para a data/hora escolhida!");
        }
        Instrucao instrucao = new Instrucao(
                null,
                aluno,
                instrutor,
                dados.dataHora(),
                false,
                null
        );
        Instrucao salva = repository.save(instrucao);
        return new DadosDetalhamentoAgendamento(salva);
    }

    @Transactional
    public DadosDetalhamentoAgendamento cancelar(DadosCancelamentoInstrucao dados) {
        Instrucao instrucao = repository.findById(dados.idInstrucao())
                .orElseThrow(() ->
                        new InstrucaoNotFoundException("ID da instrução informado não existe!"));

        if (instrucao.isCancelada()) {
            throw new ValidacaoException("Instrução já está cancelada!");
        }

        long antecedenciaHoras = Duration.between(LocalDateTime.now(), instrucao.getDataHora()).toHours();
        if (antecedenciaHoras < 24) {
            throw new ValidacaoException("Cancelamento deve ser feito com antecedência mínima de 24 horas!");
        }

        instrucao.cancelar(dados.motivo());
        Instrucao salva = repository.save(instrucao);
        return new DadosDetalhamentoAgendamento(salva);
    }

    private Instrutor escolherInstrutor(DadosAgendamentoInstrucao dados) {
        if (dados.idInstrutor() != null) {
            return instrutorRepository.getReferenceById(dados.idInstrutor());
        }
        if (dados.especialidade() == null) {
            throw new ValidacaoException("Especialidade é campo obrigatório, caso o instrutor não seja informado!");
        }
        return instrutorRepository.escolherInstrutorAleatorioDisponivel(
                dados.especialidade(),
                dados.dataHora()
        );
    }
}