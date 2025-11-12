package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.dto.PontosDTO;
import br.com.eduquizle.api.entidades.RankingUsuario;
import br.com.eduquizle.api.services.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @PostMapping("/adicionar-pontos")
    public ResponseEntity<?> adicionarPontos(@RequestBody PontosDTO dto) {
        try {
            rankingService.adicionarPontos(
                    dto.getLogin(),
                    dto.getPontos(),
                    dto.getMateria()
            );
            return ResponseEntity.ok(Map.of("status", "sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.internalServerError().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyRanking(@RequestParam String login) {
        try {
            RankingUsuario ranking = rankingService.getRankingForUser(login);
            return ResponseEntity.ok(ranking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/top100")
    public ResponseEntity<List<RankingUsuario>> getTop100Ranking(
            @RequestParam(required = false) String materia
    ) {
        List<RankingUsuario> top100 = rankingService.getTop100(materia);
        return ResponseEntity.ok(top100);
    }
}