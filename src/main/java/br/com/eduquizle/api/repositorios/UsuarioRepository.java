package br.com.eduquizle.api.repositorios;

import br.com.eduquizle.api.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}
