package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.DesafioDiario;
import br.com.eduquizle.api.entidades.enums.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DesafioDiarioRepository extends JpaRepository<DesafioDiario, Long> {

    Optional<DesafioDiario> findByDataAndMateria(LocalDate data, Materia materia);
    List<DesafioDiario> findByData(LocalDate data);
}