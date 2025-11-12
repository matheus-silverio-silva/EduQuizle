package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.RankingUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingUsuarioRepository extends JpaRepository<RankingUsuario, Long> {
    Optional<RankingUsuario> findByUsuarioLogin(String login);
    List<RankingUsuario> findTop100ByOrderByPontuacaoTotalDesc();
    List<RankingUsuario> findTop100ByOrderByPontuacaoGeografiaDesc();
    List<RankingUsuario> findTop100ByOrderByPontuacaoHistoriaDesc();
    List<RankingUsuario> findTop100ByOrderByPontuacaoBiologiaDesc();
    List<RankingUsuario> findTop100ByOrderByPontuacaoQuimicaDesc();
}