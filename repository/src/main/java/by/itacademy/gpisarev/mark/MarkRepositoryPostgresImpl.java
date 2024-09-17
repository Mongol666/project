package by.itacademy.gpisarev.mark;


import by.itacademy.gpisarev.secondary.Mark;
import by.itacademy.gpisarev.secondary.Subject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static by.itacademy.gpisarev.constants.Queries.deleteMarkByID;
import static by.itacademy.gpisarev.constants.Queries.findAllMarks;
import static by.itacademy.gpisarev.constants.Queries.findMarkByID;
import static by.itacademy.gpisarev.constants.Queries.findMarksByStudentID;
import static by.itacademy.gpisarev.constants.Queries.findSubjectByID;
import static by.itacademy.gpisarev.constants.Queries.putMark;
import static by.itacademy.gpisarev.constants.Queries.updateCountOfMarkByID;
import static by.itacademy.gpisarev.constants.Queries.updateDateOfMarkByID;
import static by.itacademy.gpisarev.constants.Queries.updateSubjectOfMarkByID;

@Slf4j
@Repository
public class MarkRepositoryPostgresImpl implements MarkRepository {
    private final ComboPooledDataSource pool;

    @Autowired
    public MarkRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    @Override
    public boolean createMark(Mark mark, int studentID) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        log.debug("Попытка добавить оценку");
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(putMark);
            save = con.setSavepoint();

            st.setInt(1, studentID);
            st.setInt(2, mark.getSubject().getId());
            st.setDate(3, Date.valueOf(mark.getDateOfMark()));
            st.setInt(4, mark.getMark());
            if (st.executeUpdate() > 0) {
                log.info("Оценка успешно добавлена");
                con.commit();
                return true;
            } else {
                log.error("Оценка не добавлена");
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
    public Mark getMarkByID(int id) {
        log.debug("Попытка найти оценку по ID");
        ResultSet setForMark = null;
        ResultSet setForSubject = null;

        try (Connection con = pool.getConnection();
             PreparedStatement stForFindMark = con.prepareStatement(findMarkByID);
             PreparedStatement stForFindSubjectToMark = con.prepareStatement(findSubjectByID)) {

            stForFindMark.setInt(1, id);
            setForMark = stForFindMark.executeQuery();

            if (setForMark.next()) {
                log.info("Оценка найдена");

                stForFindSubjectToMark.setInt(1, setForMark.getInt(4));
                setForSubject = stForFindSubjectToMark.executeQuery();

                if (setForSubject.next()) {
                    log.info("Предмет найден (для оценки)");
                    return new Mark()
                            .withId(setForMark.getInt(1))
                            .withSubject(createSubjectFromSet(setForSubject))
                            .withDateOfMark(setForMark.getDate(2).toLocalDate())
                            .withMark(setForMark.getInt(3));
                } else {
                    log.error("Не найден предмет, по которому выставлялась оценка");
                    return null;
                }
            } else {
                log.error("Оценка не найдена");
                return null;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return null;
        } finally {
            closeResource(setForMark);
            closeResource(setForMark);
            closeResource(setForSubject);
        }
    }

    @Override
    public Set<Mark> getAllMarks() {
        log.debug("Попытка получения всех оценок");
        Set<Mark> marks = new HashSet<>();
        ResultSet setForMark = null;
        ResultSet setForSubject = null;

        try (Connection con = pool.getConnection();
             PreparedStatement stForAllMarks = con.prepareStatement(findAllMarks);
             PreparedStatement stForFindSubjectToMark = con.prepareStatement(findSubjectByID)) {
            setForMark = stForAllMarks.executeQuery();

            while (setForMark.next()) {
                log.info("Оценка найдена");

                stForFindSubjectToMark.setInt(1, setForMark.getInt(4));
                setForSubject = stForFindSubjectToMark.executeQuery();

                if (setForSubject.next()) {
                    log.info("Предмет найден (для оценки)");
                    marks.add(new Mark()
                            .withId(setForMark.getInt(1))
                            .withSubject(createSubjectFromSet(setForSubject))
                            .withDateOfMark(setForMark.getDate(2).toLocalDate())
                            .withMark(setForMark.getInt(3)));
                } else {
                    log.error("Не найден предмет, по которому выставлялась оценка");
                }
            }
            return marks;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return marks;
        } finally {
            closeResource(setForMark);
            closeResource(setForSubject);
        }
    }

    @Override
    public Set<Mark> getMarksByStudentID(int studentID) {
        log.debug("Попытка получения всех оценок студента №" + studentID);
        Set<Mark> marks = new HashSet<>();
        ResultSet setForMark = null;
        ResultSet setForSubject = null;

        try (Connection con = pool.getConnection();
             PreparedStatement stForAllStudentMarks = con.prepareStatement(findMarksByStudentID);
             PreparedStatement stForFindSubjectToMark = con.prepareStatement(findSubjectByID))
        {
            stForAllStudentMarks.setInt(1, studentID);

            setForMark = stForAllStudentMarks.executeQuery();
            while (setForMark.next()) {
                log.info("Оценка найдена");

                stForFindSubjectToMark.setInt(1, setForMark.getInt(4));
                setForSubject = stForFindSubjectToMark.executeQuery();

                if (setForSubject.next()) {
                    log.info("Предмет найден (для оценки)");
                    marks.add(new Mark()
                            .withId(setForMark.getInt(1))
                            .withSubject(createSubjectFromSet(setForSubject))
                            .withDateOfMark(setForMark.getDate(2).toLocalDate())
                            .withMark(setForMark.getInt(3)));
                } else {
                    log.error("Не найден предмет, по которому выставлялась оценка");
                }
            }
            return marks;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return marks;
        } finally {
            closeResource(setForMark);
            closeResource(setForSubject);
        }
    }

    @Override
    public boolean updateMark(Mark newMark) {
        if (updateSubjectMarkById(newMark.getId(), newMark.getSubject())) {
            log.info("Предмет, по которому выставлялась оценка, обновлён");
        } else {
            log.error("Предмет, по которому выставлялась оценка, не обновлён");
            return false;
        }

        if (updateDateOfMarkById(newMark.getId(), newMark.getDateOfMark())) {
            log.info("Дата выставления оценки обновлена");
        } else {
            log.error("Дата выставления оценки не обновлена");
            return false;
        }

        if (updateMarkById(newMark.getId(), newMark.getMark())) {
            log.info("Значение оценки обновлено");
        } else {
            log.error("Значение оценки не обновлено");
            return false;
        }

        log.info("Оценка обновлена");
        return true;
    }

    @Override
    public boolean deleteMarkById(int id) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(deleteMarkByID);
            save = con.setSavepoint();

            st.setInt(1, id);
            if (st.executeUpdate() > 0) {
                log.info("Оценка удалена");
                con.commit();
                return true;
            } else {
                log.error("Ошибка удаления");
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

    private Subject createSubjectFromSet(ResultSet setForSubject) throws SQLException {
        return new Subject()
                .withId(setForSubject.getInt(1))
                .withName(setForSubject.getString(2));
    }


    private boolean updateSubjectMarkById(int id, Subject newSubject) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateSubjectOfMarkByID);
            save = con.setSavepoint();

            st.setInt(1, newSubject.getId());
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Оценка обновлена (обновлён предмет)");
                con.commit();
                return true;
            } else {
                log.error("Оценка не обновлена (не обновлён предмет)");
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

    private boolean updateDateOfMarkById(int id, LocalDate newDateOfMark) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateDateOfMarkByID);
            save = con.setSavepoint();

            st.setDate(1, Date.valueOf(newDateOfMark));
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Оценка обновлена (обновлена дата оценки)");
                con.commit();
                return true;
            } else {
                log.error("Ошибка обновления (не обновлена дата оценки)");
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

    private boolean updateMarkById(int id, int newMark) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateCountOfMarkByID);
            save = con.setSavepoint();

            st.setInt(1, newMark);
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Оценка обновлена (сама оценка обновлена)");
                con.commit();
                return true;
            } else {
                log.error("Ошибка обновления (сама оценка не обновлена)");
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

    private void myRollback(Connection con, Savepoint save) {
        try {
            if (con != null) {
                con.rollback(save);
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

