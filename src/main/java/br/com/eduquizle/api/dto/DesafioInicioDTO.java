package br.com.eduquizle.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesafioInicioDTO {
    private int id;
    private List<String> cabecalhos;
}