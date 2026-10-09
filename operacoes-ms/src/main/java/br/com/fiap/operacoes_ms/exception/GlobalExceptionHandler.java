package br.com.fiap.operacoes_ms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import feign.FeignException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handlerRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlerValidacao(
            MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(
            IllegalStateException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeign(FeignException ex) {
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Falha voluntarios-ms: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(erro);
    }
}