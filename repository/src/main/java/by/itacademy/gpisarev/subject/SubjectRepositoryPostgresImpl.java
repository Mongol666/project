package by.itacademy.gpisarev.subject;


import by.itacademy.gpisarev.secondary.Subject;
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

import static by.itacademy.gpisarev.constants.Queries.deleteSubjectByID;
import static by.itacademy.gpisarev.constants.Queries.findAllSubjects;
import static by.itacademy.gpisarev.constants.Queries.findSubjectByID;
import static by.itacademy.gpisarev.constants.Queries.findSubjectByName;
import static by.itacademy.gpisarev.constants.Queries.findSubjectsByGroupID;
import static by.itacademy.gpisarev.constants.Queries.putSubject;
import static by.itacademy.gpisarev.constants.Queries.updateSubjectNameByID;

@Slf4j
@Repository
public class SubjectRepositoryPostgresImpl implements SubjectRepository {
    private final ComboPooledDataSource pool;

    @Autowired
    public SubjectRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    @Override
    public boolean createSubject(Subject subject) {
        log.info("Попытка найти предмет");
        Subject findableSubject = getSubjectByName(subject.getName());
        if (findableSubject != null) {
            log.error("Ошибка добавления: такой предмет уже существует");
            return false;
        } else {
            Connection con = null;
            PreparedStatement st = null;
            Savepoint save = null;
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                st = con.prepareStatement(putSubject);
                save = con.setSavepoint();

                st.setString(1, subject.getName());
                if (st.executeUpdate() > 0) {
                    log.info("Предмет успешно добавлен");
                    con.commit();
                    return true;
                } else {
                    log.error("Предмет не добавлен");
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
    }

    @Override
    public Subject getSubjectById(int id) {
        log.debug("Попытка взять предмет по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSubjectByID)) {
            st.setInt(1, id);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Предмет найден");
                return new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2));
            } else {
                log.error("Предмет не найден");
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
    public Subject getSubjectByName(String name) {
        log.debug("Попытка взять предмет по названию");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSubjectByName)) {
            st.setString(1, name);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Берём предмет");
                return new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2));
            } else {
                log.error("Предмет не найден");
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
    public Set<Subject> getSubjectsByGroupID(int groupID) {
        Set<Subject> subjects = new HashSet<>();
        log.debug("Попытка взять все предметы");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSubjectsByGroupID)) {
            st.setInt(1, groupID);
            set = st.executeQuery();
            while (set.next()) {
                log.info("Берём предмет");
                subjects.add(new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2)));
            }
            return subjects;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return subjects;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public Set<Subject> getAllSubjects() {
        Set<Subject> subjects = new HashSet<>();
        log.debug("Попытка взять все предметы");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllSubjects)) {
            set = st.executeQuery();
            while (set.next()) {
                log.info("Берём предмет");
                subjects.add(new Subject()
                        .withId(set.getInt(1))
                        .withName(set.getString(2)));
            }
            return subjects;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return subjects;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public boolean updateSubject(Subject newSubject) {
        if (updateSubjectNameById(newSubject.getId(), newSubject.getName())) {
            log.info("Предмет обновлён");
            return true;
        } else {
            log.error("Предмет не обновлён");
            return false;
        }
    }

    private boolean updateSubjectNameById(int id, String newName) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateSubjectNameByID);
            save = con.setSavepoint();

            st.setString(1, newName);
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение предмета прошло успешно");
                con.commit();
                return true;
            } else {
                log.error("Предмет не найден, изменений не произошло");
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
    public boolean deleteSubjectById(int id) {
        Connection con = null;
        PreparedStatement stForDeleteSubject = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForDeleteSubject = con.prepareStatement(deleteSubjectByID);
            save = con.setSavepoint();

            stForDeleteSubject.setInt(1, id);
            if (stForDeleteSubject.executeUpdate() > 0) {
                log.info("Удаление предмета прошло успешно");
                con.commit();
                return true;
            } else {
                log.error("Предмет не удалён");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForDeleteSubject);
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
