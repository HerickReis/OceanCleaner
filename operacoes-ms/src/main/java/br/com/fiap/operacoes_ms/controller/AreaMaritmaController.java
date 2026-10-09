package br.com.fiap.operacoes_ms.controller;

import br.com.fiap.operacoes_ms.dto.AreaMaritimaDto;
import br.com.fiap.operacoes_ms.dto.AreaMaritimaExibicaoDto;
import br.com.fiap.operacoes_ms.service.AreaMaritimaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/areas-maritimas")
public class AreaMaritmaController {

    @Autowired
    private AreaMaritimaService service;

    @PostMapping
    public ResponseEntity<AreaMaritimaExibicaoDto> cadastrar(
            @RequestBody @Valid AreaMaritimaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<AreaMaritimaExibicaoDto>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaMaritimaExibicaoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaMaritimaExibicaoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AreaMaritimaDto dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}