package by.itacademy.pisarev.credential;

import by.itacademy.pisarev.AbstractRepoJpa;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Set;

@Slf4j
public class CredentialRepositoryJpaImpl extends AbstractRepoJpa<Credentials> implements CredentialRepository {

    private static volatile CredentialRepositoryJpaImpl instance;

    private CredentialRepositoryJpaImpl(SessionFactory factory) {
        super(factory, Credentials.class);
    }

    public static CredentialRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (CredentialRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new CredentialRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }

    @Override
    public boolean createCredential(Credentials credentials) {
        return create(credentials);
    }

    @Override
    public Credentials getCredentialById(int id) {
        return getByID(id);
    }

    @Override
    public Credentials getCredentialByLoginAndPassword(String login, String password) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        Credentials result = null;
        try {
            transaction.begin();
            result = manager
                    .createNamedQuery("getCredentialsByLoginAndPassword", Credentials.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
            if (result != null) {
                log.info("Учётные данные найдены по значениям логина и пароля");
            } else {
                log.error("Учётные данные не найдены по значениям логина и пароля");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения учётных данных по значениям логина и пароля: " + e.getMessage());
        } finally {
            manager.close();
        }
        return result;
    }

    @Override
    public Set<Credentials> getAllCredentials() {
        return getAll();
    }

    @Override
    public boolean updateCredential(Credentials credentials) {
        return update(credentials);
    }

    @Override
    public boolean deleteCredentialById(int id) {
        Credentials optionalCredentials = getCredentialById(id);
        if (optionalCredentials != null) {
            return remove(optionalCredentials);
        } else {
            log.error("{} не найдены, удаления не произошло", Credentials.class.getName());
            return false;
        }
    }

    @Override
    public boolean deleteCredentialByLoginAndPassword(String login, String password) {
        Credentials optionalCredentials = getCredentialByLoginAndPassword(login, password);
        if (optionalCredentials != null) {
            return remove(optionalCredentials);
        } else {
            log.error("{} не найдены, удаления не произошло", Credentials.class.getName());
            return false;
        }
    }
}
