package br.com.eduquizle.api.controllers;

import br.com.eduquizle.api.dto.CadastroRequestDTO;
import br.com.eduquizle.api.dto.LoginRequestDTO;
import br.com.eduquizle.api.entidades.Usuario;
import br.com.eduquizle.api.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // A anotação @RequestBody mapeia o JSON vindo do frontend para o nosso DTO

        // Lógicas que antes estavam no Servlet
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
}