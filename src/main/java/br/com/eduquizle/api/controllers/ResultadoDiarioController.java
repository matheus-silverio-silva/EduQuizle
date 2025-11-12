package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.dto.ResultadoDiarioDTO;
import br.com.eduquizle.api.entidades.ResultadoDiario;
import br.com.eduquizle.api.services.ResultadoDiarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resultados-diarios")
public class ResultadoDiarioController {

    @Autowired
    private ResultadoDiarioService resultadoService;

    @PostMapping
    public ResponseEntity<?> registrarResultado(@Valid @RequestBody ResultadoDiarioDTO dto) {
        try {
            ResultadoDiario novoResultado = resultadoService.registrarResultado(dto);
            return new ResponseEntity<>(novoResultado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<List<ResultadoDiario>> getResultadosDoUsuario(@RequestParam String login) {
        List<ResultadoDiario> resultados = resultadoService.buscarResultadosPorUsuario(login);
        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/desafio/{desafioId}")
    public ResponseEntity<List<ResultadoDiario>> getResultadosDoDesafio(@PathVariable Long desafioId) {
        List<ResultadoDiario> resultados = resultadoService.buscarResultadosPorDesafio(desafioId);
        return ResponseEntity.ok(resultados);
    }
}