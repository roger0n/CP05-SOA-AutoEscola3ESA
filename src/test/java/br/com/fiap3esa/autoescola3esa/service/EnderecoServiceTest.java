package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.endereco.CepInvalidoException;
import br.com.fiap3esa.autoescola3esa.domain.endereco.CepNaoEncontradoException;
import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.infra.externo.viacep.ViaCepClient;
import br.com.fiap3esa.autoescola3esa.infra.externo.viacep.ViaCepResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnderecoServiceTest {

    @Mock
    private ViaCepClient client;

    @InjectMocks
    private EnderecoService service;

    @Test
    void deveConverterRespostaDoViaCepEmDadosEndereco() {
        when(client.buscar("01001000")).thenReturn(new ViaCepResponse(
                "01001-000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP", null));

        DadosEndereco endereco = service.consultarPorCep("01001-000");

        assertThat(endereco.logradouro()).isEqualTo("Praça da Sé");
        assertThat(endereco.cidade()).isEqualTo("São Paulo");
        assertThat(endereco.uf()).isEqualTo("SP");
        assertThat(endereco.cep()).isEqualTo("01001000");
        assertThat(endereco.numero()).isNull();
    }

    @Test
    void deveRejeitarCepComQuantidadeDeDigitosInvalida() {
        assertThatThrownBy(() -> service.consultarPorCep("123"))
                .isInstanceOf(CepInvalidoException.class);
        verifyNoInteractions(client);
    }

    @Test
    void deveLancarExcecaoQuandoCepNaoExiste() {
        when(client.buscar("99999999")).thenReturn(new ViaCepResponse(null, null, null, null, null, null, true));

        assertThatThrownBy(() -> service.consultarPorCep("99999999"))
                .isInstanceOf(CepNaoEncontradoException.class);
    }
}
