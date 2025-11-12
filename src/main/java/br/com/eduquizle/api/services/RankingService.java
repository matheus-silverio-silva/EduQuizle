package br.com.eduquizle.api.services;

import br.com.eduquizle.api.entidades.RankingUsuario;
import br.com.eduquizle.api.entidades.Usuario;
import br.com.eduquizle.api.repositorios.RankingUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RankingService {

    @Autowired
    private RankingUsuarioRepository rankingRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public RankingUsuario getRankingForUser(String login) {
        return rankingRepository.findByUsuarioLogin(login)
                .orElseGet(() -> {
                    Usuario usuario = usuarioService.buscarPorLogin(login);
                    if (usuario == null) {
                        throw new IllegalArgumentException("Usuário não encontrado: " + login);
                    }
                    RankingUsuario newRanking = new RankingUsuario(usuario);
                    return rankingRepository.save(newRanking);
                });
    }

    @Transactional
    public void adicionarPontos(String login, int pontosGanhos, String materia) {
        RankingUsuario ranking = getRankingForUser(login);

        Long pontuacaoGeralAtual = ranking.getPontuacaoTotal();
        ranking.setPontuacaoTotal(pontuacaoGeralAtual + pontosGanhos);

        if (materia != null) {
            switch (materia.toLowerCase()) {
                case "geografia":
                    Long pontuacaoAtualGeo = ranking.getPontuacaoGeografia();
                    ranking.setPontuacaoGeografia(pontuacaoAtualGeo + pontosGanhos);
                    break;
                case "historia":
                    Long pontuacaoAtualHist = ranking.getPontuacaoHistoria();
                    ranking.setPontuacaoHistoria(pontuacaoAtualHist + pontosGanhos);
                    break;
                case "biologia":
                    Long pontuacaoAtualBio = ranking.getPontuacaoBiologia();
                    ranking.setPontuacaoBiologia(pontuacaoAtualBio + pontosGanhos);
                    break;
                case "quimica":
                    Long pontuacaoAtualQui = ranking.getPontuacaoQuimica();
                    ranking.setPontuacaoQuimica(pontuacaoAtualQui + pontosGanhos);
                    break;
            }
        }
        rankingRepository.save(ranking);
    }

    @Transactional(readOnly = true)
    public List<RankingUsuario> getTop100(String materia) {

        if (materia == null || materia.equalsIgnoreCase("GERAL")) {
            return rankingRepository.findTop100ByOrderByPontuacaoTotalDesc();
        }

        return switch (materia.toUpperCase()) {
            case "GEOGRAFIA" -> rankingRepository.findTop100ByOrderByPontuacaoGeografiaDesc();
            case "HISTORIA" -> rankingRepository.findTop100ByOrderByPontuacaoHistoriaDesc();
            case "BIOLOGIA" -> rankingRepository.findTop100ByOrderByPontuacaoBiologiaDesc();
            case "QUIMICA" -> rankingRepository.findTop100ByOrderByPontuacaoQuimicaDesc();
            default -> rankingRepository.findTop100ByOrderByPontuacaoTotalDesc(); // Fallback
        };
    }
}