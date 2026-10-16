package br.com.fiap3esa.autoescola3esa.domain.endereco;

public class CepNaoEncontradoException extends RuntimeException {
    public CepNaoEncontradoException(String message) {
        super(message);
    }
}
