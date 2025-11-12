package br.com.eduquizle.api.services;

import br.com.eduquizle.api.dto.DesafioDiarioDTO;
import br.com.eduquizle.api.entidades.DesafioDiario;
import br.com.eduquizle.api.entidades.Resposta;
import br.com.eduquizle.api.entidades.enums.Materia;
import br.com.eduquizle.api.repositorios.DesafioDiarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DesafioDiarioService {

    @Autowired
    private DesafioDiarioRepository desafioRepository;

    @Autowired
    private RespostaService respostaService;

    @Transactional
    public DesafioDiario criarDesafio(DesafioDiarioDTO dto) {
        Optional<DesafioDiario> existente = desafioRepository
                .findByDataAndMateria(dto.getData(), dto.getMateria());

        if (existente.isPresent()) {
            throw new IllegalStateException("Já existe um desafio para esta data e matéria.");
        }
        Resposta resposta = respostaService.findRespostaById(dto.getRespostaId())
                .orElseThrow(() -> new EntityNotFoundException("Resposta com ID " + dto.getRespostaId() + " não encontrada."));

        DesafioDiario novoDesafio = new DesafioDiario();
        novoDesafio.setData(dto.getData());
        novoDesafio.setMateria(dto.getMateria());
        novoDesafio.setResposta(resposta);

        return desafioRepository.save(novoDesafio);
    }

    @Transactional(readOnly = true)
    public DesafioDiario buscarDesafio(LocalDate data, Materia materia) {
        return desafioRepository.findByDataAndMateria(data, materia)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Desafio não encontrado para a data " + data + " e matéria " + materia
                ));
    }

    @Transactional(readOnly = true)
    public DesafioDiario buscarDesafioDeHoje(Materia materia) {
        LocalDate hoje = LocalDate.now();
        return buscarDesafio(hoje, materia);
    }

    @Transactional(readOnly = true)
    public DesafioDiario buscarPorId(Long id) {
        return desafioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Desafio com ID " + id + " não encontrado."));
    }
}