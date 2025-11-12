package br.com.eduquizle.api.services;

import br.com.eduquizle.api.dto.ComparacaoDTO;
import br.com.eduquizle.api.dto.DesafioInicioDTO;
import br.com.eduquizle.api.dto.PistaDTO;
import br.com.eduquizle.api.entidades.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DesafioService {

    @Autowired
    private RespostaService respostaService;

    @Transactional(readOnly = true)
    public DesafioInicioDTO iniciarDesafioLivre(String materia) {
        Optional<? extends Resposta> respostaOpt = respostaService.getRandomRespostaByMateria(materia);
        if (respostaOpt.isEmpty()) {
            throw new IllegalArgumentException("Matéria inválida ou sem dados disponíveis: " + materia);
        }
        Resposta resposta = respostaOpt.get();
        List<String> cabecalhos = determinarCabecalhos(materia);
        return new DesafioInicioDTO(resposta.getId_resposta(), cabecalhos);
    }

    public List<String> determinarCabecalhos(String materia) {
        return switch (materia.toLowerCase()) {
            case "geografia" -> List.of("Palpite", "Continente", "Sub-Região", "Capital", "Idioma", "Moeda", "População");
            case "historia" -> List.of("Palpite", "Ano", "País/Região", "Figura Chave", "Período", "Tema");
            case "biologia" -> List.of("Palpite", "Reino", "Filo/Divisão", "Classe", "Habitat", "Alimentação");
            case "quimica" -> List.of("Palpite", "Símbolo", "Nº Atômico", "Grupo", "Período", "Família", "Estado Físico", "Massa Atômica");
            default -> throw new IllegalArgumentException("Matéria desconhecida para determinar cabeçalhos: " + materia);
        };
    }

    @Transactional(readOnly = true)
    public ComparacaoDTO comparar(String palpiteNome, Integer respostaId) {
        Optional<? extends Resposta> respostaCorretaOpt = respostaService.findRespostaById(respostaId);
        if (respostaCorretaOpt.isEmpty()) {
            throw new IllegalArgumentException("Resposta correta com ID " + respostaId + " não encontrada.");
        }
        Resposta respostaCorreta = respostaCorretaOpt.get();

        String materia = determinarMateria(respostaCorreta);

        Optional<? extends Resposta> palpiteOpt = respostaService.findByNomeAndMateria(palpiteNome, materia);
        if (palpiteOpt.isEmpty()) {
            throw new IllegalArgumentException("Palpite '" + palpiteNome + "' não encontrado para a matéria " + materia + ".");
        }
        Resposta palpite = palpiteOpt.get();

        if (!palpite.getClass().equals(respostaCorreta.getClass())) {
            throw new IllegalArgumentException("Palpite e Resposta pertencem a matérias diferentes.");
        }

        Map<String, PistaDTO> pistas = new LinkedHashMap<>();
        boolean acertou = palpite.getId_resposta() == respostaCorreta.getId_resposta();
        pistas.put("Palpite", new PistaDTO(palpite.getNome(), acertou ? "correto" : "incorreto", null));

        if (palpite instanceof Geografia palpiteGeo && respostaCorreta instanceof Geografia respostaGeo) {
            compararGeografia(palpiteGeo, respostaGeo, pistas);
        } else if (palpite instanceof Historia palpiteHist && respostaCorreta instanceof Historia respostaHist) {
            compararHistoria(palpiteHist, respostaHist, pistas);
        } else if (palpite instanceof Biologia palpiteBio && respostaCorreta instanceof Biologia respostaBio) {
            compararBiologia(palpiteBio, respostaBio, pistas);
        } else if (palpite instanceof Quimica palpiteQuim && respostaCorreta instanceof Quimica respostaQuim) {
            compararQuimica(palpiteQuim, respostaQuim, pistas);
        }

        ComparacaoDTO resultadoDTO = new ComparacaoDTO(acertou, pistas);
        if (!acertou && respostaService.getRandomRespostaByMateria(materia).isEmpty()) { // Exemplo: Preencher se for a última tentativa
            resultadoDTO.setRespostaCorreta(respostaCorreta.getNome());
        } else if (acertou) {
            resultadoDTO.setRespostaCorreta(respostaCorreta.getNome());
        }
        return resultadoDTO;
    }

    private void compararGeografia(Geografia palpite, Geografia resposta, Map<String, PistaDTO> pistas) {
        pistas.put("Continente", new PistaDTO(
                palpite.getContinente() != null ? palpite.getContinente().toString() : "-", // Trata null
                palpite.getContinente() != null && palpite.getContinente() == resposta.getContinente() ? "correto" : "incorreto",
                null
        ));
        String subRegiaoPalpite = palpite.getSubRegiao() != null ? palpite.getSubRegiao() : "-";
        String subRegiaoResposta = resposta.getSubRegiao(); // Pode ser null
        pistas.put("Sub-Região", new PistaDTO(
                subRegiaoPalpite,
                subRegiaoPalpite.equalsIgnoreCase(subRegiaoResposta) ? "correto" : "incorreto",
                null
        ));
        String capitalPalpite = palpite.getCapital() != null ? palpite.getCapital() : "-";
        String capitalResposta = resposta.getCapital(); // Pode ser null
        pistas.put("Capital", new PistaDTO(
                capitalPalpite,
                capitalPalpite.equalsIgnoreCase(capitalResposta) ? "correto" : "incorreto",
                null
        ));
        String idiomaPalpite = palpite.getIdiomaPrincipal() != null ? palpite.getIdiomaPrincipal() : "-";
        String idiomaResposta = resposta.getIdiomaPrincipal(); // Pode ser null
        pistas.put("Idioma", new PistaDTO(
                idiomaPalpite,
                idiomaPalpite.equalsIgnoreCase(idiomaResposta) ? "correto" : "incorreto",
                null
        ));
        String moedaPalpite = palpite.getMoeda() != null ? palpite.getMoeda() : "-";
        String moedaResposta = resposta.getMoeda(); // Pode ser null
        pistas.put("Moeda", new PistaDTO(
                moedaPalpite,
                moedaPalpite.equalsIgnoreCase(moedaResposta) ? "correto" : "incorreto",
                null
        ));
        Long popPalpite = palpite.getPopulacaoAprox();
        Long popResposta = resposta.getPopulacaoAprox();
        String statusPop = "incorreto";
        String direcaoPop = null;
        String textoPop = "-";

        if (popPalpite != null && popResposta != null) {
            textoPop = String.format("%,d", popPalpite);
            if (popPalpite.equals(popResposta)) {
                statusPop = "correto";
            } else {
                direcaoPop = popPalpite < popResposta ? "maior" : "menor";
                if (Math.abs(popPalpite - popResposta) < (popResposta * 0.2)) {
                    statusPop = "parcial";
                } else {
                    statusPop = "incorreto";
                }
            }
        } else if (popPalpite != null) {
            textoPop = String.format("%,d", popPalpite);
        }

        pistas.put("População", new PistaDTO(textoPop, statusPop, direcaoPop));
    }

    private void compararHistoria(Historia palpite, Historia resposta, Map<String, PistaDTO> pistas) {
        Integer anoPalpite = palpite.getAnoAcontecimento();
        Integer anoResposta = resposta.getAnoAcontecimento();
        String statusAno = "incorreto";
        String direcaoAno = null;
        String textoAno = "-";

        if (anoPalpite != null && anoResposta != null) {
            textoAno = String.valueOf(anoPalpite);
            if (anoPalpite.equals(anoResposta)) {
                statusAno = "correto";
            } else {
                direcaoAno = anoPalpite < anoResposta ? "maior" : "menor";
                if (Math.abs(anoPalpite - anoResposta) <= 50) { // +/- 50 anos
                    statusAno = "parcial";
                } else {
                    statusAno = "incorreto";
                }
            }
        } else if (anoPalpite != null) {
            textoAno = String.valueOf(anoPalpite);
        }
        pistas.put("Ano", new PistaDTO(textoAno, statusAno, direcaoAno));
        String paisRegiaoPalpite = palpite.getPaisRegiao() != null ? palpite.getPaisRegiao() : "-";
        String paisRegiaoResposta = resposta.getPaisRegiao();
        pistas.put("País/Região", new PistaDTO(
                paisRegiaoPalpite,
                paisRegiaoPalpite.equalsIgnoreCase(paisRegiaoResposta) ? "correto" : "incorreto",
                null
        ));
        String figuraChavePalpite = palpite.getFiguraChave() != null ? palpite.getFiguraChave() : "-";
        String figuraChaveResposta = resposta.getFiguraChave();
        pistas.put("Figura Chave", new PistaDTO(
                figuraChavePalpite,
                figuraChavePalpite.equalsIgnoreCase(figuraChaveResposta) ? "correto" : "incorreto",
                null
        ));
        String periodoPalpite = palpite.getPeriodoEpoca() != null ? palpite.getPeriodoEpoca() : "-";
        String periodoResposta = resposta.getPeriodoEpoca();
        pistas.put("Período", new PistaDTO(
                periodoPalpite,
                periodoPalpite.equalsIgnoreCase(periodoResposta) ? "correto" : "incorreto",
                null
        ));
        pistas.put("Tema", new PistaDTO(
                palpite.getTema() != null ? palpite.getTema().toString() : "-", // Trata null
                palpite.getTema() != null && palpite.getTema() == resposta.getTema() ? "correto" : "incorreto",
                null
        ));
    }

    private void compararBiologia(Biologia palpite, Biologia resposta, Map<String, PistaDTO> pistas) {
        pistas.put("Reino", new PistaDTO(
                palpite.getReino() != null ? palpite.getReino().toString() : "-",
                palpite.getReino() != null && palpite.getReino() == resposta.getReino() ? "correto" : "incorreto",
                null
        ));
        String filoPalpite = palpite.getFiloDivisao() != null ? palpite.getFiloDivisao() : "-";
        String filoResposta = resposta.getFiloDivisao();
        pistas.put("Filo/Divisão", new PistaDTO(
                filoPalpite,
                filoPalpite.equalsIgnoreCase(filoResposta) ? "correto" : "incorreto",
                null
        ));
        String classePalpite = palpite.getClasse() != null ? palpite.getClasse() : "-";
        String classeResposta = resposta.getClasse();
        pistas.put("Classe", new PistaDTO(
                classePalpite,
                classePalpite.equalsIgnoreCase(classeResposta) ? "correto" : "incorreto",
                null
        ));
        pistas.put("Habitat", new PistaDTO(
                palpite.getHabitat() != null ? palpite.getHabitat().toString() : "-",
                palpite.getHabitat() != null && palpite.getHabitat() == resposta.getHabitat() ? "correto" : "incorreto",
                null
        ));
        pistas.put("Alimentação", new PistaDTO(
                palpite.getAlimentacao() != null ? palpite.getAlimentacao().toString() : "-",
                palpite.getAlimentacao() != null && palpite.getAlimentacao() == resposta.getAlimentacao() ? "correto" : "incorreto",
                null
        ));
    }

    private void compararQuimica(Quimica palpite, Quimica resposta, Map<String, PistaDTO> pistas) {
        // Símbolo (String) - Trata nulls
        String simboloPalpite = palpite.getSimbolo() != null ? palpite.getSimbolo() : "-";
        String simboloResposta = resposta.getSimbolo();
        pistas.put("Símbolo", new PistaDTO( // Removida a linha duplicada e o debug
                simboloPalpite,
                simboloPalpite.equalsIgnoreCase(simboloResposta) ? "correto" : "incorreto",
                null
        ));

        Integer numAtomicoPalpite = palpite.getNumeroAtomico();
        Integer numAtomicoResposta = resposta.getNumeroAtomico();
        String statusNumAtomico = "incorreto";
        String direcaoNumAtomico = null;
        String textoNumAtomico = "-";

        if (numAtomicoPalpite != null && numAtomicoResposta != null) {
            textoNumAtomico = String.valueOf(numAtomicoPalpite);
            if (numAtomicoPalpite.equals(numAtomicoResposta)) {
                statusNumAtomico = "correto";
            } else {
                direcaoNumAtomico = numAtomicoPalpite < numAtomicoResposta ? "maior" : "menor";
                if (Math.abs(numAtomicoPalpite - numAtomicoResposta) <= 5) { // +/- 5
                    statusNumAtomico = "parcial";
                } else {
                    statusNumAtomico = "incorreto";
                }
            }
        } else if (numAtomicoPalpite != null) {
            textoNumAtomico = String.valueOf(numAtomicoPalpite);
        }
        pistas.put("Nº Atômico", new PistaDTO(textoNumAtomico, statusNumAtomico, direcaoNumAtomico));
        pistas.put("Grupo", new PistaDTO(
                palpite.getGrupo() != null ? palpite.getGrupo().toString() : "-",
                palpite.getGrupo() != null && palpite.getGrupo().equals(resposta.getGrupo()) ? "correto" : "incorreto",
                null
        ));
        pistas.put("Período", new PistaDTO(
                palpite.getPeriodo() != null ? palpite.getPeriodo().toString() : "-",
                palpite.getPeriodo() != null && palpite.getPeriodo().equals(resposta.getPeriodo()) ? "correto" : "incorreto",
                null
        ));
        String familiaPalpite = palpite.getFamilia() != null ? palpite.getFamilia() : "-";
        String familiaResposta = resposta.getFamilia();
        pistas.put("Família", new PistaDTO(
                familiaPalpite,
                familiaPalpite.equalsIgnoreCase(familiaResposta) ? "correto" : "incorreto",
                null
        ));
        pistas.put("Estado Físico", new PistaDTO(
                palpite.getEstadoFisico25C() != null ? palpite.getEstadoFisico25C().toString() : "-",
                palpite.getEstadoFisico25C() != null && palpite.getEstadoFisico25C() == resposta.getEstadoFisico25C() ? "correto" : "incorreto",
                null
        ));
        BigDecimal massaPalpite = palpite.getMassaAtomica();
        BigDecimal massaResposta = resposta.getMassaAtomica();
        String statusMassa = "incorreto";
        String direcaoMassa = null;
        String textoMassa = "-";

        if (massaPalpite != null && massaResposta != null) {
            textoMassa = massaPalpite.toPlainString();
            int comparacao = massaPalpite.compareTo(massaResposta);
            if (comparacao == 0) {
                statusMassa = "correto";
            } else {
                direcaoMassa = comparacao < 0 ? "maior" : "menor"; // Se palpite < resposta, correto é maior
                if (massaPalpite.subtract(massaResposta).abs().compareTo(BigDecimal.valueOf(1.0)) < 0) {
                    statusMassa = "parcial";
                } else {
                    statusMassa = "incorreto";
                }
            }
        } else if (massaPalpite != null) {
            textoMassa = massaPalpite.toPlainString();
        }
        pistas.put("Massa Atômica", new PistaDTO(textoMassa, statusMassa, direcaoMassa));
    }

    private String determinarMateria(Resposta resposta) {
        if (resposta instanceof Geografia) return "geografia";
        if (resposta instanceof Historia) return "historia";
        if (resposta instanceof Biologia) return "biologia";
        if (resposta instanceof Quimica) return "quimica";
        throw new IllegalStateException("Tipo de Resposta desconhecido: " + resposta.getClass().getName());
    }
}