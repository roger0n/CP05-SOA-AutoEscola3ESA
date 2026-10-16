package br.com.fiap3esa.autoescola3esa.infra.externo.viacep;

import br.com.fiap3esa.autoescola3esa.domain.endereco.ServicoExternoIndisponivelException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente HTTP da API externa ViaCEP. Recebe um CEP com 8 dígitos (já validado
 * pelo serviço) e devolve os dados do endereço correspondente.
 */
@Component
public class ViaCepClient {
    private final RestClient restClient;

    public ViaCepClient(RestClient viaCepRestClient) {
        this.restClient = viaCepRestClient;
    }

    public ViaCepResponse buscar(String cep) {
        try {
            return restClient.get()
                    .uri("/{cep}/json/", cep)
                    .retrieve()
                    .body(ViaCepResponse.class);
        } catch (RestClientException e) {
            throw new ServicoExternoIndisponivelException(
                    "Serviço de consulta de CEP (ViaCEP) indisponível no momento!", e);
        }
    }
}
