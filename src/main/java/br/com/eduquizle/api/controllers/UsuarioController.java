package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.dto.*;
import br.com.eduquizle.api.entidades.Usuario;
import br.com.eduquizle.api.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Usuario> cadastrar(@RequestBody CadastroRequestDTO request) {
        if (!request.isTermos()) {
            throw new IllegalArgumentException("É preciso aceitar os termos de uso.");
        }
        if (!request.getSenha().equals(request.getConfirmaSenha())) {
            throw new IllegalArgumentException("As senhas não conferem.");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setLogin(request.getLogin());
        novoUsuario.setNome(request.getNome());
        novoUsuario.setEmail(request.getEmail());
        novoUsuario.setSenha(request.getSenha());

        Usuario usuarioSalvo = usuarioService.registrarUsuario(novoUsuario);
        return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody LoginRequestDTO request) {
        Usuario usuario = usuarioService.autenticar(request.getLogin(), request.getSenha());

        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login ou senha inválidos.");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UpdateProfileDTO updateRequest) {
        try {
            String login = updateRequest.getLogin();

            Usuario usuarioAtualizado = usuarioService.atualizarPerfil(
                    login,
                    updateRequest.getNome(),
                    updateRequest.getEmail()
            );

            UsuarioProfileDTO profile = new UsuarioProfileDTO(usuarioAtualizado.getId(), usuarioAtualizado.getLogin(), usuarioAtualizado.getNome(), usuarioAtualizado.getEmail());
            return ResponseEntity.ok(profile);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changeMyPassword(@Valid @RequestBody ChangePasswordDTO passwordRequest) {

        if (!passwordRequest.getNovaSenha().equals(passwordRequest.getConfirmacaoNovaSenha())) {
            return ResponseEntity.badRequest().body(Map.of("erro", "A nova senha e a confirmação não coincidem."));
        }

        try {
            String login = passwordRequest.getLogin();

            usuarioService.alterarSenha(
                    login,
                    passwordRequest.getSenhaAtual(),
                    passwordRequest.getNovaSenha()
            );
            return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso."));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}