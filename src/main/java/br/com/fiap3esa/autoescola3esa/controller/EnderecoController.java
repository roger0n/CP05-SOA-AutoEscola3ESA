package br.com.fiap3esa.autoescola3esa.controller;

import br.com.fiap3esa.autoescola3esa.domain.endereco.DadosEndereco;
import br.com.fiap3esa.autoescola3esa.service.EnderecoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@RequiredArgsConstructor
@Tag(name = "Endereços", description = "Consulta de endereço por CEP (consome a API externa ViaCEP)")
public class EnderecoController {
    private final EnderecoService service;

    @GetMapping("/cep/{cep}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Consultar endereço por CEP",
            description = "Consulta a API pública ViaCEP e devolve o endereço no mesmo formato usado no cadastro de alunos e instrutores. Aceita o CEP com ou sem hífen.")
    public ResponseEntity<DadosEndereco> consultarPorCep(@PathVariable String cep) {
        return ResponseEntity.ok(service.consultarPorCep(cep));
    }
}
