package repositorios;

import entidades.Usuario;
import org.hibernate.Session;
import utils.HibernateUtil;

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
}
