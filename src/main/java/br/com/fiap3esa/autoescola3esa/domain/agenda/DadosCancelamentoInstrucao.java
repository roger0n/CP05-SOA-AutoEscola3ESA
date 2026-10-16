package br.com.fiap3esa.autoescola3esa.domain.agenda;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record DadosCancelamentoInstrucao(
        @NotNull
        @JsonProperty("id_instrucao")
        Long idInstrucao,

        @NotNull
        MotivoCancelamento motivo) {
}
