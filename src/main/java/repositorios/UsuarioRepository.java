package repositorios;

import entidades.Usuario;
import org.hibernate.Session;
import utils.HibernateUtil;

import java.util.Optional;

public class UsuarioRepository extends RepositorioGenerico<Usuario> {
    public UsuarioRepository() {
        super(Usuario.class);
    }
    public Usuario buscarPorCampoUnico(String campo, String valor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Usuario u WHERE u." + campo + " = :valor", Usuario.class)
                    .setParameter("valor", valor)
                    .uniqueResult();
        }
    }
    public Usuario buscarPorLoginESenha(String login, String senha) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM Usuario u WHERE u.login = :login AND u.senha = :senha", Usuario.class)
                    .setParameter("login", login)
                    .setParameter("senha", senha)
                    .uniqueResult();
        }
    }
}
