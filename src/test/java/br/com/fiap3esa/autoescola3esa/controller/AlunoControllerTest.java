package br.com.fiap3esa.autoescola3esa.controller;

import br.com.fiap3esa.autoescola3esa.domain.aluno.AlunoNotFoundException;
import br.com.fiap3esa.autoescola3esa.domain.aluno.DadosDetalhamentoAluno;
import br.com.fiap3esa.autoescola3esa.domain.aluno.DadosListagemAluno;
import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.autoescola3esa.service.AlunoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de camada web em modo standalone (sem subir o contexto Spring nem o banco).
 * A segurança (@PreAuthorize) não é aplicada aqui; o foco é o contrato HTTP.
 */
@ExtendWith(MockitoExtension.class)
class AlunoControllerTest {

    @Mock
    private AlunoService service;

    @InjectMocks
    private AlunoController controller;

    private MockMvc mockMvc;

    private final DadosEndereco endereco = new DadosEndereco(
            "Av. Paulista", "1106", null, "Bela Vista", "São Paulo", "SP", "01311000");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private static final String JSON_VALIDO = """
            {
              "nome": "Maria Silva",
              "email": "maria@email.com",
              "telefone": "11999999999",
              "cpf": "12345678901",
              "endereco": {
                "logradouro": "Av. Paulista", "numero": "1106", "bairro": "Bela Vista",
                "cidade": "São Paulo", "uf": "SP", "cep": "01311000"
              }
            }
            """;

    @Test
    void deveCadastrarAlunoERetornar201ComLocation() throws Exception {
        when(service.cadastrarAluno(any())).thenReturn(new DadosDetalhamentoAluno(
                1L, "Maria Silva", "maria@email.com", "11999999999", "12345678901", endereco, true));

        mockMvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/alunos/1"))
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    void deveRetornar400QuandoCadastroForInvalido() throws Exception {
        mockMvc.perform(post("/alunos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarAlunos() throws Exception {
        var pagina = new PageImpl<>(
                List.of(new DadosListagemAluno(1L, "Maria Silva", "maria@email.com", "12345678901")),
                PageRequest.of(0, 10), 1);
        when(service.listarAlunos(any())).thenReturn(pagina);

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Maria Silva"));
    }

    @Test
    void deveDetalharAluno() throws Exception {
        when(service.detalharAluno(1L)).thenReturn(new DadosDetalhamentoAluno(
                1L, "Maria Silva", "maria@email.com", "11999999999", "12345678901", endereco, true));

        mockMvc.perform(get("/alunos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    void deveRetornar404QuandoAlunoNaoExiste() throws Exception {
        when(service.detalharAluno(anyLong())).thenThrow(new AlunoNotFoundException("ID do aluno informado não existe!"));

        mockMvc.perform(get("/alunos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ID do aluno informado não existe!"));
    }

    @Test
    void deveExcluirAlunoERetornar204() throws Exception {
        mockMvc.perform(delete("/alunos/1"))
                .andExpect(status().isNoContent());
    }
}
