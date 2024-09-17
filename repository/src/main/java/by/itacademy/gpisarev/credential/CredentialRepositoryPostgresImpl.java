package by.itacademy.gpisarev.credential;

import by.itacademy.gpisarev.credentials.Credentials;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashSet;
import java.util.Set;

import static by.itacademy.gpisarev.constants.Queries.deleteCredentialsByID;
import static by.itacademy.gpisarev.constants.Queries.deleteCredentialsByLoginAndPassword;
import static by.itacademy.gpisarev.constants.Queries.findAllCredentials;
import static by.itacademy.gpisarev.constants.Queries.findCredentialsByID;
import static by.itacademy.gpisarev.constants.Queries.findCredentialsByLoginAndPassword;
import static by.itacademy.gpisarev.constants.Queries.putCredentials;
import static by.itacademy.gpisarev.constants.Queries.updateCredentials;

@Slf4j
@Repository
public class CredentialRepositoryPostgresImpl implements CredentialRepository {
    private final ComboPooledDataSource pool;

    @Autowired
    public CredentialRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    @Override
    public boolean createCredential(Credentials credentials) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        log.debug("Попытка найти в репозитории учётные данные");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(putCredentials);
            save = con.setSavepoint();

            Credentials credential = getCredentialByLoginAndPassword(credentials.getLogin(), credentials.getPassword());
            if (credential == null) {
                log.info("Таких учётных данных не существует, вносим в таблицу");
                st.setString(1, credentials.getLogin());
                st.setString(2, credentials.getPassword());
                if (st.executeUpdate() > 0) {
                    log.info("Добавлены новые учётные данные");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка добавления: SQLException");
                    con.rollback(save);
                    return false;
                }
            } else {
                log.error("Ошибка добавления: такие учётные данные уже существуют");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка добавления: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    @Override
    public Credentials getCredentialById(int id) {
        log.debug("Попытка взять учётные данные по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findCredentialsByID)) {
            st.setInt(1, id);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Берём учётные данные");
                return new Credentials()
                        .withId(set.getInt(1))
                        .withLogin(set.getString(2))
                        .withPassword(set.getString(3));
            } else {
                log.error("Учётные данные не найдены");
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return null;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public Credentials getCredentialByLoginAndPassword(String login, String password) {
        log.debug("Попытка взять учётные данные по логину и паролю");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findCredentialsByLoginAndPassword)) {
            st.setString(1, login);
            st.setString(2, password);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Берём учётные данные из репозитория");
                return new Credentials()
                        .withId(set.getInt(1))
                        .withLogin(set.getString(2))
                        .withPassword(set.getString("password"));
            } else {
                log.error("Учётные данные не найдены");
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return null;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public Set<Credentials> getAllCredentials() {
        log.info("Берём все учётные данные");
        Set<Credentials> credentials = new HashSet<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllCredentials)) {
            set = st.executeQuery();
            if (set.next()) {
                credentials.add(new Credentials()
                        .withId(set.getInt(1))
                        .withLogin(set.getString("login"))
                        .withPassword(set.getString(3)));
            } else {
                log.error("Учётные данные не найдены");
                return credentials;
            }
            return credentials;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return credentials;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public boolean updateCredential(Credentials credentials) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateCredentials);
            save = con.setSavepoint();

            st.setString(1, credentials.getLogin());
            st.setString(2, credentials.getPassword());
            if (st.executeUpdate() > 0) {
                log.info("Изменение учётных данных в репозитории");
                con.commit();
                return true;
            } else {
                log.error("Учётные данные не найдены, изменений не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    @Override
    public boolean deleteCredentialById(int id) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(deleteCredentialsByID);
            save = con.setSavepoint();

            st.setInt(1, id);
            if (st.executeUpdate() > 0) {
                log.info("Удаление учётных данных из репозитория");
                con.commit();
                return true;
            } else {
                log.error("Учётные данные не найдены, удаления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    @Override
    public boolean deleteCredentialByLoginAndPassword(String login, String password) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(deleteCredentialsByLoginAndPassword);
            save = con.setSavepoint();
            st.setString(1, login);
            st.setString(2, password);
            if (st.executeUpdate() > 0) {
                log.info("Удаление учётных данных из репозитория");
                con.commit();
                return true;
            } else {
                log.error("Учётные данные не найдены, удаления не произошло");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    private void myRollback(Connection connection, Savepoint firstSavePoint) {
        try {
            if (connection != null) {
                connection.rollback(firstSavePoint);
            }
        } catch (SQLException ex) {
            log.error("Rollback не удался");
        }
    }

    private void closeResource(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

