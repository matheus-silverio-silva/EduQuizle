package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.Biologia;
import br.com.eduquizle.api.entidades.Quimica;
import br.com.eduquizle.api.entidades.enums.ReinoBiologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuimicaRepository extends JpaRepository<Quimica, Integer> {

    @SuppressWarnings("JpaQlInspection")
    @Query(value = "SELECT g.nome FROM Quimica g")
    List<String> findAllNomes();
    Optional<Quimica> findByNomeIgnoreCase(String nome);
    Optional<Quimica> findByNumeroAtomico(Integer numeroAtomico);
}