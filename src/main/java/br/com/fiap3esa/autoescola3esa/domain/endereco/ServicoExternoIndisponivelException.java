package br.com.fiap3esa.autoescola3esa.domain.endereco;

public class ServicoExternoIndisponivelException extends RuntimeException {
    public ServicoExternoIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}
