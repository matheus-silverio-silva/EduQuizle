package br.com.eduquizle.api.services;

import br.com.eduquizle.api.dto.ResultadoDiarioDTO;
import br.com.eduquizle.api.entidades.DesafioDiario;
import br.com.eduquizle.api.entidades.ResultadoDiario;
import br.com.eduquizle.api.entidades.Usuario;
import br.com.eduquizle.api.repositorios.DesafioDiarioRepository;
import br.com.eduquizle.api.repositorios.ResultadoDiarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResultadoDiarioService {

    @Autowired
    private ResultadoDiarioRepository resultadoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private DesafioDiarioRepository desafioRepository;

    @Transactional
    public ResultadoDiario registrarResultado(ResultadoDiarioDTO dto) {
        Optional<ResultadoDiario> existente = resultadoRepository
                .findByUsuarioLoginAndDesafioId(dto.getLogin(), dto.getDesafioId());

        if (existente.isPresent()) {
            throw new IllegalStateException("O usuário já registrou um resultado para este desafio.");
        }

        Usuario usuario = usuarioService.buscarPorLogin(dto.getLogin());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado: " + dto.getLogin());
        }

        DesafioDiario desafio = desafioRepository.findById(dto.getDesafioId())
                .orElseThrow(() -> new IllegalArgumentException("Desafio não encontrado: " + dto.getDesafioId()));

        ResultadoDiario novoResultado = new ResultadoDiario();
        novoResultado.setUsuario(usuario);
        novoResultado.setDesafio(desafio);
        novoResultado.setPontuacao(dto.getPontuacao());
        novoResultado.setTentativas(dto.getTentativas());
        novoResultado.setTempoGastoSegundos(dto.getTempoGastoSegundos());
        novoResultado.setDataConclusao(LocalDateTime.now());

        return resultadoRepository.save(novoResultado);
    }

    @Transactional(readOnly = true)
    public List<ResultadoDiario> buscarResultadosPorUsuario(String login) {
        return resultadoRepository.findByUsuarioLogin(login);
    }

    @Transactional(readOnly = true)
    public List<ResultadoDiario> buscarResultadosPorDesafio(Long desafioId) {
        return resultadoRepository.findByDesafioId(desafioId);
    }
}