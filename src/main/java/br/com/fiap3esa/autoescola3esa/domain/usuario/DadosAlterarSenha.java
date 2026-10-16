package br.com.fiap3esa.autoescola3esa.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosAlterarSenha(
        @NotBlank
        String senhaAtual,

        @NotBlank
        String novaSenha) {
}
