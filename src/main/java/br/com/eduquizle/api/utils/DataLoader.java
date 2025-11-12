package br.com.eduquizle.api.utils;

import br.com.eduquizle.api.dto.DesafioDiarioDTO;
import br.com.eduquizle.api.entidades.*;
import br.com.eduquizle.api.entidades.enums.*;
import br.com.eduquizle.api.repositorios.*;
import br.com.eduquizle.api.services.DesafioDiarioService;
import br.com.eduquizle.api.services.RespostaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Autowired private GeografiaRepository geografiaRepository;
    @Autowired private QuimicaRepository quimicaRepository;
    @Autowired private BiologiaRepository biologiaRepository;
    @Autowired private HistoriaRepository historiaRepository;

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RankingUsuarioRepository rankingRepository;
    @Autowired private DesafioDiarioService desafioDiarioService;
    @Autowired private RespostaService respostaService;

    @Override
    public void run(String... args) throws Exception {

        log.info("--- INICIANDO DATA LOADER (MATÉRIAS) ---");
        carregarTodosOsCSVs();
        log.info("--- DATA LOADER (MATÉRIAS) FINALIZADO ---");

        log.info("--- INICIANDO DATA LOADER (USUÁRIOS E RANKING) ---");
        popularUsuariosEHighscores();
        log.info("--- DATA LOADER (USUÁRIOS E RANKING) FINALIZADO ---");

        log.info("--- INICIANDO DATA LOADER (DESAFIOS DIÁRIOS) ---");
        popularDesafiosDiarios(LocalDate.now().minusDays(1));
        popularDesafiosDiarios(LocalDate.now());
        popularDesafiosDiarios(LocalDate.now().plusDays(1));
        log.info("--- DATA LOADER (DESAFIOS DIÁRIOS) FINALIZADO ---");
    }

    private void carregarTodosOsCSVs() {
        Function<String[], Geografia> mapeadorGeografia = (dados) -> {
            Geografia g = new Geografia();
            g.setNome(dados[0]);
            g.setContinente(Continente.valueOf(dados[1]));
            g.setSubRegiao(dados[2]);
            g.setCapital(dados[3]);
            g.setIdiomaPrincipal(dados[4]);
            g.setMoeda(dados[5]);
            g.setPopulacaoAprox(Long.parseLong(dados[6]));
            return g;
        };
        carregarDadosGenericos("/data/geografia.csv", geografiaRepository, mapeadorGeografia, 7);

        Function<String[], Quimica> mapeadorQuimica = (dados) -> {
            Quimica q = new Quimica();
            q.setNome(dados[0]);
            q.setSimbolo(dados[1]);
            q.setNumeroAtomico(Integer.parseInt(dados[2]));
            q.setGrupo(Integer.parseInt(dados[3]));
            q.setPeriodo(Integer.parseInt(dados[4]));
            q.setFamilia(dados[5]);
            q.setEstadoFisico25C(EstadoFisico.valueOf(dados[6]));
            q.setMassaAtomica(new BigDecimal(dados[7]));
            return q;
        };
        carregarDadosGenericos("/data/quimica.csv", quimicaRepository, mapeadorQuimica, 8);

        Function<String[], Biologia> mapeadorBiologia = (dados) -> {
            Biologia b = new Biologia();
            b.setNome(dados[0]);
            b.setReino(ReinoBiologico.valueOf(dados[1]));
            b.setFiloDivisao(dados[2]);
            b.setClasse(dados[3]);
            b.setHabitat(Habitat.valueOf(dados[4]));
            b.setAlimentacao(Alimentacao.valueOf(dados[5]));
            return b;
        };
        carregarDadosGenericos("/data/biologia.csv", biologiaRepository, mapeadorBiologia, 6);

        Function<String[], Historia> mapeadorHistoria = (dados) -> {
            Historia h = new Historia();
            h.setNome(dados[0]);
            h.setAnoAcontecimento(Integer.parseInt(dados[1]));
            h.setPaisRegiao(dados[2]);
            h.setFiguraChave(dados[3]);
            h.setPeriodoEpoca(dados[4]);
            h.setTema(TemaHistorico.valueOf(dados[5]));
            return h;
        };
        carregarDadosGenericos("/data/historia.csv", historiaRepository, mapeadorHistoria, 6);
    }

    private <T extends Resposta> void carregarDadosGenericos(String caminhoArquivo, JpaRepository<T, Integer> repository, Function<String[], T> mapeador, int numCampos) {
        if (repository.count() > 0) {
            log.info(">>> Dados para {} já carregados. Pulando.", caminhoArquivo);
            return;
        }

        log.info(">>> Carregando dados de {}...", caminhoArquivo);
        String linha = "";
        String separador = ",";

        try (InputStream is = getClass().getResourceAsStream(caminhoArquivo);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            br.readLine();

            while ((linha = br.readLine()) != null) {
                linha = linha.replaceAll("\"", "");
                String[] dados = linha.split(separador, -1);

                if (dados.length >= numCampos) {
                    try {
                        T entidade = mapeador.apply(dados);
                        repository.save(entidade);
                    } catch (Exception e) {
                        log.warn("Erro ao processar linha: [{}]. Erro: {}", linha, e.getMessage());
                    }
                } else {
                    log.warn("Linha mal formatada pulada: [{}]", linha);
                }
            }
        } catch (Exception e) {
            log.error("Erro ao carregar dados de " + caminhoArquivo + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void popularUsuariosEHighscores() {
        log.info("Verificando usuários e rankings de teste...");

        criarUsuarioComRankingSeNaoExistir("aluno1", "Aluno Um", "aluno1@email.com", "senha123", 150L, 100L, 50L, 0L, 0L);
        criarUsuarioComRankingSeNaoExistir("jogador2", "Jogador Dois", "jogador2@email.com", "senha123", 250L, 0L, 50L, 200L, 0L);
        criarUsuarioComRankingSeNaoExistir("teste", "Usuário Teste", "teste@email.com", "senha123", 100L, 100L, 0L, 0L, 0L);
    }

    private void criarUsuarioComRankingSeNaoExistir(String login, String nome, String email, String senha,
                                                    Long pTotal, Long pGeo, Long pHist, Long pBio, Long pQuim) {

        Optional<Usuario> userOpt = usuarioRepository.findByLogin(login);
        if (userOpt.isPresent()) {
            log.info(">>> Usuário '{}' já existe. Pulando criação.", login);
            return;
        }

        try {
            Usuario novoUsuario = new Usuario();
            novoUsuario.setLogin(login);
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setSenha(senha);

            Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

            RankingUsuario novoRanking = new RankingUsuario(usuarioSalvo);
            novoRanking.setPontuacaoTotal(pTotal);
            novoRanking.setPontuacaoGeografia(pGeo);
            novoRanking.setPontuacaoHistoria(pHist);
            novoRanking.setPontuacaoBiologia(pBio);
            novoRanking.setPontuacaoQuimica(pQuim);

            rankingRepository.save(novoRanking);

            log.info(">>> Usuário '{}' e seu ranking criados com sucesso.", login);

        } catch (Exception e) {
            log.error(">>> ERRO ao criar usuário '{}': {}", login, e.getMessage(), e);
        }
    }

    private void popularDesafiosDiarios(LocalDate data) {
        log.info("Verificando desafios diários para a data: {}", data);

        for (Materia materia : Materia.values()) {
            try {
                Optional<? extends Resposta> respostaOpt = respostaService.getRandomRespostaByMateria(materia.name());

                if (respostaOpt.isEmpty()) {
                    log.warn(">>> [DESAFIO] Nenhuma resposta de {} encontrada. Não é possível criar desafio.", materia);
                    continue;
                }

                Resposta respostaAleatoria = respostaOpt.get();
                Integer respostaId = respostaAleatoria.getId_resposta();
                DesafioDiarioDTO dto = new DesafioDiarioDTO(data, materia, respostaId);

                desafioDiarioService.criarDesafio(dto);
                log.info(">>> SUCESSO: Desafio de {} para {} criado.", materia, data);

            } catch (IllegalStateException e) {
                log.info(">>> INFO: Desafio de {} para {} já existe. Pulando.", materia, data);
            } catch (Exception e) {
                log.error(">>> ERRO INESPERADO ao criar desafio de {}: {}", materia, e.getMessage());
            }
        }
    }
}
