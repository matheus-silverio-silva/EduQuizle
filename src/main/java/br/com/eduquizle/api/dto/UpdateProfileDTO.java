package br.com.eduquizle.api.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateProfileDTO {
    @NotBlank private String login;
    @NotBlank private String nome;
    @NotBlank @Email private String email;
}