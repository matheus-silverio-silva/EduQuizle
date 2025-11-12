package br.com.eduquizle.api.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioProfileDTO {
    private Long id;
    private String login;
    private String nome;
    private String email;

}
