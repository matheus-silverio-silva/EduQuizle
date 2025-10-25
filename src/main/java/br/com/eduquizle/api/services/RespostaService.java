package br.com.eduquizle.api.services;

import br.com.eduquizle.api.entidades.*;
import br.com.eduquizle.api.repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RespostaService {

    @Autowired private GeografiaRepository geografiaRepository;
    @Autowired private HistoriaRepository historiaRepository;
    @Autowired private BiologiaRepository biologiaRepository;
    @Autowired private QuimicaRepository quimicaRepository;

    @Transactional(readOnly = true)
    public List<String> getAllNomesByMateria(String materia) {
        return switch (materia.toLowerCase()) {
            case "geografia" -> geografiaRepository.findAllNomes();
            case "historia" -> historiaRepository.findAllNomes();
            case "biologia" -> biologiaRepository.findAllNomes();
            case "quimica" -> quimicaRepository.findAllNomes();
            default -> Collections.emptyList();
        };
    }

    @Transactional(readOnly = true)
    public Optional<? extends Resposta> getRandomRespostaByMateria(String materia) {
        JpaRepository<? extends Resposta, Integer> repository = getRepositoryByMateria(materia);
        if (repository == null) {
            return Optional.empty();
        }
        long count = repository.count();
        if (count == 0) {
            return Optional.empty();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(0, (int) count);
        List<? extends Resposta> randomList = repository.findAll(PageRequest.of(randomIndex, 1)).getContent();
        return randomList.isEmpty() ? Optional.empty() : Optional.of(randomList.get(0));
    }

    @Transactional(readOnly = true)
    public Optional<? extends Resposta> findByNomeAndMateria(String nome, String materia) {
        return switch (materia.toLowerCase()) {
            case "geografia" -> geografiaRepository.findByNomeIgnoreCase(nome);
            case "historia" -> historiaRepository.findByNomeIgnoreCase(nome);
            case "biologia" -> biologiaRepository.findByNomeIgnoreCase(nome);
            case "quimica" -> quimicaRepository.findByNomeIgnoreCase(nome);
            default -> Optional.empty();
        };
    }

    @Transactional(readOnly = true)
    public Optional<? extends Resposta> findRespostaById(Integer id) {
        // Tenta buscar em cada repositório.
        Optional<Geografia> geoOpt = geografiaRepository.findById(id);
        if (geoOpt.isPresent()) return geoOpt;

        Optional<Historia> histOpt = historiaRepository.findById(id);
        if (histOpt.isPresent()) return histOpt;

        Optional<Biologia> bioOpt = biologiaRepository.findById(id);
        if (bioOpt.isPresent()) return bioOpt;

        Optional<Quimica> quimOpt = quimicaRepository.findById(id);
        // Sem if/else no último, apenas retorna o Optional (pode estar vazio)
        return quimOpt;
    }

    public JpaRepository<? extends Resposta, Integer> getRepositoryByMateria(String materia) {
        if (materia == null) {
            return null;
        }
        String materiaLower = materia.toLowerCase();
        if ("geografia".equals(materiaLower)) {
            return geografiaRepository;
        } else if ("historia".equals(materiaLower)) {
            return historiaRepository;
        } else if ("biologia".equals(materiaLower)) {
            return biologiaRepository;
        } else if ("quimica".equals(materiaLower)) {
            return quimicaRepository;
        } else {
            return null; // Matéria desconhecida
        }
    }
}