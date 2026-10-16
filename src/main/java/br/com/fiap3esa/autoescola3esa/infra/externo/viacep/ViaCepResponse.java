package br.com.fiap3esa.autoescola3esa.infra.externo.viacep;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Resposta da API pública ViaCEP (https://viacep.com.br).
 * Para CEPs inexistentes a API responde 200 com {"erro": true}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        Boolean erro) {
}
