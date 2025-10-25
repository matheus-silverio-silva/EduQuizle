package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.Biologia;
import br.com.eduquizle.api.entidades.Historia;
import br.com.eduquizle.api.entidades.enums.ReinoBiologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriaRepository extends JpaRepository<Historia, Integer> {

    @SuppressWarnings("JpaQlInspection")
    @Query(value = " SELECT g.nome FROM Historia g")
    List<String> findAllNomes();
    Optional<Historia> findByNomeIgnoreCase(String nome);
    Optional<Historia> findByAnoAcontecimento(Integer anoAcontecimento);
}