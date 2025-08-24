package repositorios;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import utils.HibernateUtil;

import java.util.List;

public class RepositorioGenerico<T> {

    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private final Class<T> clazz;

    public RepositorioGenerico(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Long inserir(T entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Long id = (Long) session.save(entity);
            tx.commit();
            return id;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void update(T entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void excluir(T entity) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public T pesquisaId(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(clazz, id);
        }
    }

    public List<T> retornaTodos() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from " + clazz.getName(), clazz).getResultList();
        }
    }
}