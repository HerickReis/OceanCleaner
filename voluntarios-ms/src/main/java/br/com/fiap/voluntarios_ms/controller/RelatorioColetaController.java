package br.com.fiap.voluntarios_ms.controller;

import br.com.fiap.voluntarios_ms.dto.RelatorioColetaDto;
import br.com.fiap.voluntarios_ms.dto.RelatorioColetaExibicaoDto;
import br.com.fiap.voluntarios_ms.service.RelatorioColetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioColetaController {

    @Autowired
    private RelatorioColetaService service;

    @PostMapping
    public ResponseEntity<RelatorioColetaExibicaoDto> cadastrar(
            @RequestBody @Valid RelatorioColetaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<RelatorioColetaExibicaoDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RelatorioColetaExibicaoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/operacao/{idOperacao}")
    public ResponseEntity<List<RelatorioColetaExibicaoDto>> listarPorOperacao(
            @PathVariable Long idOperacao) {
        return ResponseEntity.ok(service.listarPorOperacao(idOperacao));
    }

    @GetMapping("/voluntario/{idVoluntario}")
    public ResponseEntity<List<RelatorioColetaExibicaoDto>> listarPorVoluntario(
            @PathVariable Long idVoluntario) {
        return ResponseEntity.ok(service.listarPorVoluntario(idVoluntario));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}