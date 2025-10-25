package br.com.eduquizle.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PistaDTO {
    private String texto;
    private String status;
    private String direcao;
}