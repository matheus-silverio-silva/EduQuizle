package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.Biologia;
import br.com.eduquizle.api.entidades.enums.ReinoBiologico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BiologiaRepository extends JpaRepository<Biologia, Long> {

    Optional<Biologia> findByNomeIgnoreCase(String nome);
    Optional<Biologia> findByReino(ReinoBiologico reino);
}