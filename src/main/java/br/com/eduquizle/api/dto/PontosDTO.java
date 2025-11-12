package br.com.eduquizle.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PontosDTO {
    private String login;
    private int pontos;
    private String materia;
}