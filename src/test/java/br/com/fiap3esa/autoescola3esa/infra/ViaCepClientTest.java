package br.com.fiap3esa.autoescola3esa.infra;

import br.com.fiap3esa.autoescola3esa.domain.endereco.ServicoExternoIndisponivelException;
import br.com.fiap3esa.autoescola3esa.infra.externo.viacep.ViaCepClient;
import br.com.fiap3esa.autoescola3esa.infra.externo.viacep.ViaCepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** Testa o cliente HTTP sem acessar a internet: a resposta do ViaCEP é simulada. */
class ViaCepClientTest {

    private MockRestServiceServer server;
    private ViaCepClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://viacep.com.br/ws");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ViaCepClient(builder.build());
    }

    @Test
    void deveConsultarCepEMapearResposta() {
        String json = """
                {"cep":"01001-000","logradouro":"Praça da Sé","complemento":"lado ímpar","unidade":"",
                 "bairro":"Sé","localidade":"São Paulo","uf":"SP","estado":"São Paulo","ibge":"3550308","ddd":"11"}
                """;
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        ViaCepResponse resposta = client.buscar("01001000");

        assertThat(resposta.logradouro()).isEqualTo("Praça da Sé");
        assertThat(resposta.localidade()).isEqualTo("São Paulo");
        assertThat(resposta.uf()).isEqualTo("SP");
        assertThat(resposta.erro()).isNull();
        server.verify();
    }

    @Test
    void deveInterpretarFlagDeErroDeCepInexistente() {
        server.expect(requestTo("https://viacep.com.br/ws/99999999/json/"))
                .andRespond(withSuccess("{\"erro\": true}", MediaType.APPLICATION_JSON));

        ViaCepResponse resposta = client.buscar("99999999");

        assertThat(resposta.erro()).isTrue();
    }

    @Test
    void deveLancarExcecaoQuandoServicoExternoFalhar() {
        server.expect(requestTo("https://viacep.com.br/ws/01001000/json/"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.buscar("01001000"))
                .isInstanceOf(ServicoExternoIndisponivelException.class);
    }
}
