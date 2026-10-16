package br.com.fiap3esa.autoescola3esa.controller;

import br.com.fiap3esa.autoescola3esa.domain.endereco.CepInvalidoException;
import br.com.fiap3esa.autoescola3esa.domain.endereco.CepNaoEncontradoException;
import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.domain.endereco.ServicoExternoIndisponivelException;
import br.com.fiap3esa.autoescola3esa.infra.exception.TratadorGlobalErros;
import br.com.fiap3esa.autoescola3esa.service.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EnderecoControllerTest {

    @Mock
    private EnderecoService service;

    @InjectMocks
    private EnderecoController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TratadorGlobalErros())
                .build();
    }

    @Test
    void deveConsultarEnderecoPorCep() throws Exception {
        when(service.consultarPorCep("01001000")).thenReturn(new DadosEndereco(
                "Praça da Sé", null, "lado ímpar", "Sé", "São Paulo", "SP", "01001000"));

        mockMvc.perform(get("/enderecos/cep/01001000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    void deveRetornar400ParaCepInvalido() throws Exception {
        when(service.consultarPorCep("123")).thenThrow(new CepInvalidoException("CEP deve conter 8 dígitos numéricos!"));

        mockMvc.perform(get("/enderecos/cep/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404ParaCepInexistente() throws Exception {
        when(service.consultarPorCep("99999999")).thenThrow(new CepNaoEncontradoException("CEP informado não foi encontrado!"));

        mockMvc.perform(get("/enderecos/cep/99999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar503QuandoViaCepEstiverIndisponivel() throws Exception {
        when(service.consultarPorCep("01001000"))
                .thenThrow(new ServicoExternoIndisponivelException("Serviço de consulta de CEP (ViaCEP) indisponível no momento!", null));

        mockMvc.perform(get("/enderecos/cep/01001000"))
                .andExpect(status().isServiceUnavailable());
    }
}
