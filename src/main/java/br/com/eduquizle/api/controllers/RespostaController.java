package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.entidades.Resposta;
import br.com.eduquizle.api.services.RespostaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/respostas")
public class RespostaController {

    @Autowired
    private RespostaService respostaService;

    @GetMapping("/nomes")
    public ResponseEntity<List<String>> getListaNomesPorMateria(@RequestParam String materia) {
        List<String> nomes = respostaService.getAllNomesByMateria(materia);
        if (respostaService.getRepositoryByMateria(materia) == null && nomes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nomes);
    }

    @GetMapping("/buscar")
    public ResponseEntity<? extends Resposta> getRespostaPorNomeEMateria(@RequestParam String nome, @RequestParam String materia) {
        Optional<? extends Resposta> respostaOpt = respostaService.findByNomeAndMateria(nome, materia);
        return respostaOpt.<ResponseEntity<? extends Resposta>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}