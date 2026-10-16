package br.com.fiap3esa.autoescola3esa.service;

import br.com.fiap3esa.autoescola3esa.domain.endereco.CepInvalidoException;
import br.com.fiap3esa.autoescola3esa.domain.endereco.CepNaoEncontradoException;
import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.infra.externo.viacep.ViaCepClient;
import br.com.fiap3esa.autoescola3esa.infra.externo.viacep.ViaCepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnderecoService {
    private final ViaCepClient viaCepClient;

    public DadosEndereco consultarPorCep(String cepInformado) {
        String cep = cepInformado == null ? "" : cepInformado.replaceAll("\\D", "");
        if (cep.length() != 8) {
            throw new CepInvalidoException("CEP deve conter 8 dígitos numéricos!");
        }

        ViaCepResponse resposta = viaCepClient.buscar(cep);
        if (resposta == null || Boolean.TRUE.equals(resposta.erro())) {
            throw new CepNaoEncontradoException("CEP informado não foi encontrado!");
        }

        return new DadosEndereco(
                resposta.logradouro(),
                null,
                resposta.complemento(),
                resposta.bairro(),
                resposta.localidade(),
                resposta.uf(),
                cep
        );
    }
}
