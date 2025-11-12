package br.com.eduquizle.api.dto;

import br.com.eduquizle.api.entidades.enums.Materia;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesafioDiarioDTO {

    private LocalDate data;
    private Materia materia;
    private Integer respostaId;

}