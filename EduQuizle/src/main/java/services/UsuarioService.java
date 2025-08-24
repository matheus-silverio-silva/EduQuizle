package services;

import entidades.Usuario;
import repositorios.UsuarioRepository;

public class UsuarioService {

    private final UsuarioRepository usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioRepository();
    }
    public Usuario buscarPorLogin(String login) {
        return usuarioDAO.buscarPorCampoUnico("login", login);
    }
    public Usuario buscarPorEmail(String email) {
        return usuarioDAO.buscarPorCampoUnico("email", email);
    }
    public Long registrarUsuario(Usuario usuario) {
        validarUsuario(usuario);
        return usuarioDAO.inserir(usuario);
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
