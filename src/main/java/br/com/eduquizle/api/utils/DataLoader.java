package br.com.eduquizle.api.utils;

import br.com.eduquizle.api.entidades.Biologia;
import br.com.eduquizle.api.entidades.Geografia;
import br.com.eduquizle.api.entidades.Historia;
import br.com.eduquizle.api.entidades.Quimica;
import br.com.eduquizle.api.entidades.Resposta;
import br.com.eduquizle.api.entidades.enums.*;
import br.com.eduquizle.api.repositorios.BiologiaRepository;
import br.com.eduquizle.api.repositorios.GeografiaRepository;
import br.com.eduquizle.api.repositorios.HistoriaRepository;
import br.com.eduquizle.api.repositorios.QuimicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private GeografiaRepository geografiaRepository;
    @Autowired private QuimicaRepository quimicaRepository;
    @Autowired private BiologiaRepository biologiaRepository;
    @Autowired private HistoriaRepository historiaRepository;

    @Override
    public void run(String... args) throws Exception {

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
        carregarDadosGenericos("/data/geografia.csv", geografiaRepository, mapeadorGeografia);

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
        carregarDadosGenericos("/data/quimica.csv", quimicaRepository, mapeadorQuimica);

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
        carregarDadosGenericos("/data/biologia.csv", biologiaRepository, mapeadorBiologia);

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
        carregarDadosGenericos("/data/historia.csv", historiaRepository, mapeadorHistoria);
    }

    private <T extends Resposta> void carregarDadosGenericos(String caminhoArquivo, JpaRepository<T, ?> repository, Function<String[], T> mapeador) {
        if (repository.count() > 0) {
            System.out.println(">>> Dados para " + caminhoArquivo + " já carregados. Pulando.");
            return;
        }

        System.out.println(">>> Carregando dados de " + caminhoArquivo + "...");
        String linha = "";
        String separador = ",";

        try (InputStream is = getClass().getResourceAsStream(caminhoArquivo);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            br.readLine();

            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(separador, -1);
                T entidade = mapeador.apply(dados);
                repository.save(entidade);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar dados de " + caminhoArquivo + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}