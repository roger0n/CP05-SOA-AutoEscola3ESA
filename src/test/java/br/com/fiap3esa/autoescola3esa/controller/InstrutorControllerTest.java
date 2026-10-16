package br.com.fiap3esa.autoescola3esa.controller;

import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.DadosDetalhamentoInstrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.DadosListagemInstrutor;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.Especialidade;
import br.com.fiap3esa.autoescola3esa.domain.instrutor.InstrutorNotFoundException;
import br.com.fiap3esa.autoescola3esa.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.autoescola3esa.service.InstrutorService;
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

@ExtendWith(MockitoExtension.class)
class InstrutorControllerTest {

    @Mock
    private InstrutorService service;

    @InjectMocks
    private InstrutorController controller;

    private MockMvc mockMvc;

    private final DadosEndereco endereco = new DadosEndereco(
            "Rua das Flores", "50", null, "Centro", "Osasco", "SP", "06010000");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private static final String JSON_VALIDO = """
            {
              "nome": "Carlos Lima",
              "email": "carlos@email.com",
              "telefone": "11988888888",
              "cnh": "123456789",
              "especialidade": "CARROS",
              "endereco": {
                "logradouro": "Rua das Flores", "numero": "50", "bairro": "Centro",
                "cidade": "Osasco", "uf": "SP", "cep": "06010000"
              }
            }
            """;

    @Test
    void deveCadastrarInstrutorERetornar201ComLocation() throws Exception {
        when(service.cadastrarInstrutor(any())).thenReturn(new DadosDetalhamentoInstrutor(
                1L, "Carlos Lima", "carlos@email.com", "11988888888", "123456789",
                Especialidade.CARROS, endereco, true));

        mockMvc.perform(post("/instrutores").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/instrutores/1"))
                .andExpect(jsonPath("$.especialidade").value("CARROS"));
    }

    @Test
    void deveRetornar400QuandoCadastroForInvalido() throws Exception {
        mockMvc.perform(post("/instrutores").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarInstrutores() throws Exception {
        var pagina = new PageImpl<>(
                List.of(new DadosListagemInstrutor(1L, "Carlos Lima", "carlos@email.com", Especialidade.CARROS)),
                PageRequest.of(0, 10), 1);
        when(service.listarInstrutores(any())).thenReturn(pagina);

        mockMvc.perform(get("/instrutores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Carlos Lima"));
    }

    @Test
    void deveRetornar404QuandoInstrutorNaoExiste() throws Exception {
        when(service.detalharInstrutor(anyLong()))
                .thenThrow(new InstrutorNotFoundException("ID do instrutor informado não existe!"));

        mockMvc.perform(get("/instrutores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("ID do instrutor informado não existe!"));
    }

    @Test
    void deveExcluirInstrutorERetornar204() throws Exception {
        mockMvc.perform(delete("/instrutores/1"))
                .andExpect(status().isNoContent());
    }
}
