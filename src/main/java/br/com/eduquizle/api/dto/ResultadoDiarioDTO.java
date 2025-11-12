package br.com.eduquizle.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoDiarioDTO {
    private String login;
    private Long desafioId;
    private int pontuacao;
    private int tentativas;
    private long tempoGastoSegundos;
}