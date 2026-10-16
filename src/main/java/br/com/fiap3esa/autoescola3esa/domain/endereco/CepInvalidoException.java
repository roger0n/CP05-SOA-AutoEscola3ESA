package br.com.fiap3esa.autoescola3esa.domain.endereco;

public class CepInvalidoException extends RuntimeException {
    public CepInvalidoException(String message) {
        super(message);
    }
}
