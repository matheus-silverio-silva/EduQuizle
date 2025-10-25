package br.com.eduquizle.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
public class ComparacaoDTO {

    private boolean acertou;

    private String respostaCorreta;

    private Map<String, PistaDTO> pistas;
    public ComparacaoDTO(boolean acertou, Map<String, PistaDTO> pistas) {
        this.acertou = acertou;
        this.pistas = pistas;
    }
}