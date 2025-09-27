package br.com.eduquizle.api.services;

import br.com.eduquizle.api.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.eduquizle.api.repositorios.UsuarioRepository;

import static org.hibernate.internal.util.StringHelper.isBlank;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login).orElse(null);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String login, String senha) {
        if (isBlank(login) || isBlank(senha)) return null;
        return usuarioRepository.findByLoginAndSenha(login.trim(), senha.trim()).orElse(null);
    }
    private void validarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        if (usuario.getNome() == null || usuario.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (usuario.getLogin() == null || usuario.getLogin().isBlank()) {
            throw new IllegalArgumentException("O login é obrigatório.");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        if (!usuario.getEmail().matches("^[\\w\\.-]+@[\\w\\.-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");
        }
        if (buscarPorLogin(usuario.getLogin()) != null) {
            throw new IllegalArgumentException("Login já cadastrado.");
        }
        if (buscarPorEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
    }
}