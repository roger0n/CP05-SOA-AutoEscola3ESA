package br.com.fiap3esa.autoescola3esa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.fiap3esa.autoescola3esa.domain.aluno.Aluno;
import br.com.fiap3esa.autoescola3esa.domain.aluno.AlunoRepository;
import br.com.fiap3esa.autoescola3esa.domain.agenda.DadosAgendamentoInstrucao;
import br.com.fiap3esa.autoescola3esa.domain.agenda.DadosCancelamentoInstrucao;
import br.com.fiap3esa.autoescola3esa.domain.agenda.DadosDetalhamentoAgendamento;
import br.com.fiap3esa.autoescola3esa.domain.agenda.Instrucao;
import br.com.fiap3esa.autoescola3esa.domain.agenda.InstrucaoRepository;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Instrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.InstrutorRepository;
import br.com.fiap3esa.autoescola3esa.service.AgendaDeInstrucoes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Instruções", description = "Agendamento e cancelamento de instruções")
@RequestMapping("/instrucoes")
@RequiredArgsConstructor
public class InstrucaoController {
    private final AgendaDeInstrucoes agenda;

    @Operation(summary = "Agendar instrução")
    @PostMapping
    public ResponseEntity<DadosDetalhamentoAgendamento> agendarInstrucao(@RequestBody @Valid DadosAgendamentoInstrucao dados) {
        return ResponseEntity.ok(agenda.agendar(dados));
    }

    @Operation(summary = "Cancelar instrução (antecedência mínima de 24h)")
    @PutMapping("/cancelar")
    public ResponseEntity<DadosDetalhamentoAgendamento> cancelarInstrucao(@RequestBody @Valid DadosCancelamentoInstrucao dados) {
        return ResponseEntity.ok(agenda.cancelar(dados));
    }
}