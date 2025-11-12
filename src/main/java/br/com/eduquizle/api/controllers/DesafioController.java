package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.dto.*; // Importa todos os DTOs
import br.com.eduquizle.api.entidades.DesafioDiario;
import br.com.eduquizle.api.entidades.enums.Materia;
import br.com.eduquizle.api.services.DesafioDiarioService;
import br.com.eduquizle.api.services.DesafioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/desafios")
public class DesafioController {

    @Autowired
    private DesafioService desafioService;

    @Autowired
    private DesafioDiarioService desafioDiarioService;


    @GetMapping("/livre")
    public ResponseEntity<?> iniciarDesafioLivre(@RequestParam String materia) {
        try {
            DesafioInicioDTO dto = desafioService.iniciarDesafioLivre(materia);
            Map<String, Object> response = Map.of(
                    "id", dto.getId(),
                    "cabecalhos", dto.getCabecalhos()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/comparar")
    public ResponseEntity<?> compararModoLivre(@RequestBody ComparacaoRequestDTO dto) {
        try {
            ComparacaoDTO resultado = desafioService.comparar(dto.getPalpite(), dto.getRespostaId());
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/diario")
    public ResponseEntity<?> getDesafioDeHoje(@RequestParam String materia) {
        try {
            Materia materiaEnum = Materia.valueOf(materia.toUpperCase());
            DesafioDiario desafio = desafioDiarioService.buscarDesafioDeHoje(materiaEnum);

            List<String> cabecalhos = desafioService.determinarCabecalhos(materia);

            Map<String, Object> response = Map.of(
                    "id_resposta", desafio.getResposta().getId_resposta(),
                    "id_desafio", desafio.getId(),
                    "cabecalhos", cabecalhos
            );
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Nenhum desafio de " + materia + " encontrado para hoje."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Matéria inválida: " + materia));
        }
    }

    @PostMapping("/diario/comparar")
    public ResponseEntity<?> compararModoDiario(
            @RequestBody ComparacaoDiarioDTO dto,
            @RequestParam String materia
    ) {
        try {
            Materia materiaEnum = Materia.valueOf(materia.toUpperCase());
            DesafioDiario desafioDeHoje = desafioDiarioService.buscarDesafioDeHoje(materiaEnum);
            Integer respostaIdCorreta = desafioDeHoje.getResposta().getId_resposta();

            ComparacaoDTO resultado = desafioService.comparar(dto.getPalpite(), respostaIdCorreta);

            System.out.println("Palpite Recebido: " + dto.getPalpite());
            System.out.println("ID Correto: " + respostaIdCorreta);
            System.out.println("Resultado da Comparação: " + resultado.isAcertou());

            return ResponseEntity.ok(resultado);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", "Desafio diário não encontrado."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PostMapping("/admin/diario")
    public ResponseEntity<?> criarDesafioDiarioAdmin(@RequestBody DesafioDiarioDTO dto) {
        try {
            DesafioDiario novoDesafio = desafioDiarioService.criarDesafio(dto);
            return new ResponseEntity<>(novoDesafio, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}