package by.itacademy.gpisarev.credential;

import by.itacademy.gpisarev.AbstractRepoJpa;
import by.itacademy.gpisarev.credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Set;

@Slf4j
@Repository
public class CredentialRepositoryJpaImpl extends AbstractRepoJpa<Credentials> implements CredentialRepository {

    @Autowired
    public CredentialRepositoryJpaImpl(SessionFactory factory) {
        super(factory, Credentials.class);
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
