package br.com.fiap3esa.autoescola3esa.controller;

import br.com.fiap3esa.autoescola3esa.domain.agenda.DadosDetalhamentoAgendamento;
import br.com.fiap3esa.autoescola3esa.domain.agenda.MotivoCancelamento;
import br.com.fiap3esa.autoescola3esa.domain.agenda.ValidacaoException;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Especialidade;
import br.com.fiap3esa.autoescola3esa.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.autoescola3esa.service.AgendaDeInstrucoes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InstrucaoControllerTest {

    @Mock
    private AgendaDeInstrucoes agenda;

    @InjectMocks
    private InstrucaoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .build();
    }

    @Test
    void deveAgendarInstrucao() throws Exception {
        LocalDateTime dataHora = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        when(agenda.agendar(any())).thenReturn(new DadosDetalhamentoAgendamento(
                1L, "Maria Silva", "Carlos Lima", Especialidade.CARROS, dataHora, false, null));

        String dataFormatada = dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
        String corpo = "{\"id_aluno\":1,\"id_instrutor\":2,\"data_hora\":\"" + dataFormatada + "\"}";

        mockMvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeAluno").value("Maria Silva"))
                .andExpect(jsonPath("$.cancelada").value(false));
    }

    @Test
    void deveRetornar400QuandoAgendamentoForInvalido() throws Exception {
        mockMvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar422QuandoRegraDeNegocioForViolada() throws Exception {
        LocalDateTime dataHora = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        when(agenda.agendar(any())).thenThrow(new ValidacaoException("Instrutor ocupado na data/hora escolhida!"));

        String dataFormatada = dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm"));
        String corpo = "{\"id_aluno\":1,\"id_instrutor\":2,\"data_hora\":\"" + dataFormatada + "\"}";

        mockMvc.perform(post("/instrucoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("Instrutor ocupado na data/hora escolhida!"));
    }

    @Test
    void deveCancelarInstrucao() throws Exception {
        LocalDateTime dataHora = LocalDateTime.now().plusDays(3);
        when(agenda.cancelar(any())).thenReturn(new DadosDetalhamentoAgendamento(
                1L, "Maria Silva", "Carlos Lima", Especialidade.CARROS, dataHora, true,
                MotivoCancelamento.ALUNO_DESISTIU));

        mockMvc.perform(put("/instrucoes/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id_instrucao\":1,\"motivo\":\"ALUNO_DESISTIU\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelada").value(true))
                .andExpect(jsonPath("$.motivoCancelamento").value("ALUNO_DESISTIU"));
    }
}
