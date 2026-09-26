package br.com.fiap.voluntarios_ms.controller;

import br.com.fiap.voluntarios_ms.dto.VoluntarioDto;
import br.com.fiap.voluntarios_ms.dto.VoluntarioExibicaoDto;
import br.com.fiap.voluntarios_ms.service.VoluntarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voluntarios")
public class VoluntarioController {

    @Autowired
    private VoluntarioService service;

    @PostMapping
    public ResponseEntity<VoluntarioExibicaoDto> cadastrar(
            @RequestBody @Valid VoluntarioDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<VoluntarioExibicaoDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoluntarioExibicaoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VoluntarioExibicaoDto> atualizar(
            @PathVariable Long id, @RequestBody @Valid VoluntarioDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}