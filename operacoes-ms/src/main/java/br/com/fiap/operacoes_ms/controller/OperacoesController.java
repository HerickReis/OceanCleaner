package br.com.fiap.operacoes_ms.controller;

import br.com.fiap.operacoes_ms.dto.OperacaoDto;
import br.com.fiap.operacoes_ms.dto.OperacaoExibicaoDto;
import br.com.fiap.operacoes_ms.service.OperacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operacoes")
public class OperacoesController {

    @Autowired
    private OperacaoService service;

    @PostMapping
    public ResponseEntity<OperacaoExibicaoDto> cadastrar(
            @RequestBody @Valid OperacaoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<OperacaoExibicaoDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperacaoExibicaoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperacaoExibicaoDto> atualizar(
            @PathVariable Long id, @RequestBody @Valid OperacaoDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OperacaoExibicaoDto>> listarPorStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(service.listarPorStatus(status));
    }
}