package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.dto.ComparacaoDTO;
import br.com.eduquizle.api.dto.ComparacaoRequestDTO;
import br.com.eduquizle.api.dto.DesafioInicioDTO;
import br.com.eduquizle.api.services.DesafioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/desafios")
public class DesafioController {

    @Autowired
    private DesafioService desafioService;

    @GetMapping("/livre")
    public ResponseEntity<?> iniciarDesafioLivre(@RequestParam String materia) {
        try {
            DesafioInicioDTO desafioDto = desafioService.iniciarDesafioLivre(materia);
            return ResponseEntity.ok(desafioDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar desafio livre: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno ao iniciar desafio."));
        }
    }

    @PostMapping("/comparar")
    public ResponseEntity<?> comparar(@Valid @RequestBody ComparacaoRequestDTO request) {
        try {
            ComparacaoDTO resultado = desafioService.comparar(request.getPalpite(), request.getRespostaId());
            return ResponseEntity.ok(resultado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro ao comparar palpite: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno ao processar o palpite."));
        }
    }
}