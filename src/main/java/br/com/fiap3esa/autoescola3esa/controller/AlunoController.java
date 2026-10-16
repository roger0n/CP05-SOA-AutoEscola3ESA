package br.com.fiap3esa.autoescola3esa.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.fiap3esa.autoescola3esa.domain.aluno.*;
import br.com.fiap3esa.autoescola3esa.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "Alunos", description = "CRUD de alunos (exclusão lógica)")
@RequestMapping("/alunos")
public class AlunoController {
    private final AlunoService service;

    public AlunoController(AlunoService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastrar aluno")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> cadastrarAluno(
            @RequestBody @Valid DadosCadastroAluno dados,
            UriComponentsBuilder uriBuilder) {
        DadosDetalhamentoAluno dto = service.cadastrarAluno(dados);
        URI uri = uriBuilder
                .path("/alunos/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Listar alunos ativos (paginado)")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<DadosListagemAluno>> listarAlunos(
            @PageableDefault(size = 10, sort = "nome") Pageable paginacao) {
        return ResponseEntity.ok(service.listarAlunos(paginacao));
    }

    @Operation(summary = "Detalhar aluno")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> detalharAluno(
            @PathVariable Long id) {
        return ResponseEntity.ok(service.detalharAluno(id));
    }

    @Operation(summary = "Atualizar aluno")
    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DadosDetalhamentoAluno> atualizarAluno(
            @RequestBody @Valid DadosAtualizacaoAluno dados) {
        return ResponseEntity.ok(service.atualizarAluno(dados));
    }

    @Operation(summary = "Excluir aluno (inativa o cadastro)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluirAluno(@PathVariable Long id) {
        service.excluirAluno(id);
        return ResponseEntity.noContent().build();
    }
}
