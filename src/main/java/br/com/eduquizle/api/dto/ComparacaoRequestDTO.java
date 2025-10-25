package br.com.eduquizle.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ComparacaoRequestDTO {

    @NotBlank(message = "O palpite não pode estar vazio.")
    private String palpite;
    @NotNull(message = "O ID da resposta é obrigatório.")
    private Integer respostaId;
}