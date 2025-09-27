package br.com.eduquizle.api.dto;

import lombok.Data;

@Data
public class CadastroRequestDTO {
    private String nome;
    private String login;
    private String email;
    private String senha;
    private String confirmaSenha;
    private boolean termos;
}