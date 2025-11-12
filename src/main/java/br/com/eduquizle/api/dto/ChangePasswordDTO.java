package br.com.eduquizle.api.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    @NotBlank private String login;
    @NotBlank private String senhaAtual;
    @NotBlank @Size(min = 6) private String novaSenha;
    @NotBlank private String confirmacaoNovaSenha;
}