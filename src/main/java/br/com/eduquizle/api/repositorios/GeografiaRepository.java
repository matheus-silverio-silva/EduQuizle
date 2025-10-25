package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.Biologia;
import br.com.eduquizle.api.entidades.Geografia;
import br.com.eduquizle.api.entidades.enums.ReinoBiologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeografiaRepository extends JpaRepository<Geografia, Integer> {

    @SuppressWarnings("JpaQlInspection")
    @Query(value = "SELECT g.nome FROM Geografia g")
    List<String> findAllNomes();
    Optional<Geografia> findByNomeIgnoreCase(String nome);
    Optional<Geografia> findByCapital(String capital);
}