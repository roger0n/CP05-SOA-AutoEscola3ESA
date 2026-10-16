package br.com.fiap3esa.autoescola3esa.domain.agenda;

import br.com.fiap3esa.autoescola3esa.domain.instrutor.Especialidade;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DadosAgendamentoInstrucao(
        @NotNull
        @JsonProperty("id_aluno")
        //@JsonAlias("id_aluno") //Faz a mesma coisa que o @JsonProperty
        Long idAluno,

        @JsonProperty("id_instrutor")
        Long idInstrutor,
        Especialidade especialidade,

        @NotNull
        @Future
        @Schema(type = "string", example = "15/10/2026 - 09:00", description = "Formato dd/MM/yyyy - HH:mm (hora inteira, de segunda a sábado, das 06h às 20h)")
        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
        @JsonProperty("data_hora")
        LocalDateTime dataHora) {
}