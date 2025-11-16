package br.com.eduquizle.api.services;

import br.com.eduquizle.api.dto.DesafioDiarioDTO;
import br.com.eduquizle.api.entidades.Resposta;
import br.com.eduquizle.api.entidades.enums.Materia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AgendadorDesafioService {

    private static final Logger log = LoggerFactory.getLogger(AgendadorDesafioService.class);

    @Autowired
    private RespostaService respostaService;

    @Autowired
    private DesafioDiarioService desafioDiarioService;

    @Scheduled(cron = "0 0 * * * ?")
    public void criarDesafiosDiariosAutomaticamente() {
        log.info("ROTINA AGENDADA: Iniciando verificação de desafios diários...");

        LocalDate dataDeHoje = LocalDate.now();

        for (Materia materia : Materia.values()) {

            log.info("Verificando desafio para: " + materia);

            try {
                Optional<? extends Resposta> respostaOpt = respostaService.getRandomRespostaByMateria(materia.name());

                if (respostaOpt.isEmpty()) {
                    log.warn("Nenhuma resposta encontrada para " + materia + ". Desafio não pode ser criado.");
                    continue;
                }

                Resposta respostaAleatoria = respostaOpt.get();
                Integer respostaId = respostaAleatoria.getId_resposta();

                DesafioDiarioDTO novoDesafioDTO = new DesafioDiarioDTO(dataDeHoje, materia, respostaId);
                desafioDiarioService.criarDesafio(novoDesafioDTO);

                log.info("SUCESSO: Desafio de " + materia + " criado para " + dataDeHoje);

            } catch (IllegalStateException e) {
                log.info("INFO: Desafio de " + materia + " para " + dataDeHoje + " já existe. Ignorando.");

            } catch (Exception e) {
                log.error("ERRO INESPERADO ao criar desafio para " + materia + ": " + e.getMessage(), e);
            }
        }
        log.info("ROTINA AGENDADA: Verificação de desafios concluída.");
    }
}