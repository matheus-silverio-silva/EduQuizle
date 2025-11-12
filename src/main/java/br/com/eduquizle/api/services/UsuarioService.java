package br.com.eduquizle.api.services;

import br.com.eduquizle.api.entidades.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.eduquizle.api.repositorios.UsuarioRepository;

import java.util.Optional;

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
    @Transactional(readOnly = true)
    public Usuario autenticar(String login, String senha) {
        Usuario usuario = this.buscarPorLogin(login);

        if (usuario != null && usuario.getSenha().equals(senha)) {
            return usuario;
        }
        return null;
    }

    @Transactional
    public Usuario atualizarPerfil(String login, String novoNome, String novoEmail) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        Optional<Usuario> emailExistente = usuarioRepository.findByEmail(novoEmail);
        if (emailExistente.isPresent() && !emailExistente.get().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("Este e-mail já está em uso por outra conta.");
        }

        usuario.setNome(novoNome);
        usuario.setEmail(novoEmail);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarSenha(String login, String senhaAtual, String novaSenha) {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (!usuario.getSenha().equals(senhaAtual)) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
        usuario.setSenha(novaSenha);
        usuarioRepository.save(usuario);
    }

}