package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.ResultadoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultadoDiarioRepository extends JpaRepository<ResultadoDiario, Long> {

    List<ResultadoDiario> findByUsuarioLogin(String login);
    List<ResultadoDiario> findByDesafioId(Long desafioId);
    Optional<ResultadoDiario> findByUsuarioLoginAndDesafioId(String login, Long desafioId);
}