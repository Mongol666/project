package by.itacademy.gpisarev.salary;


import by.itacademy.gpisarev.secondary.Salary;
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

import static by.itacademy.gpisarev.constants.Queries.deleteSalaryByID;
import static by.itacademy.gpisarev.constants.Queries.findAllSalaries;
import static by.itacademy.gpisarev.constants.Queries.findSalariesByDate;
import static by.itacademy.gpisarev.constants.Queries.findSalariesByTeacherID;
import static by.itacademy.gpisarev.constants.Queries.findSalaryByID;
import static by.itacademy.gpisarev.constants.Queries.putSalary;
import static by.itacademy.gpisarev.constants.Queries.updateDateOfSalary;
import static by.itacademy.gpisarev.constants.Queries.updateSalaryByID;

@Slf4j
@Repository
public class SalaryRepositoryPostgresImpl implements SalaryRepository {
    private final ComboPooledDataSource pool;

    @Autowired
    public SalaryRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    @Override
    public boolean createSalary(Salary salary, int teacherID) {
        Connection con = null;
        PreparedStatement stForInsertSalary = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForInsertSalary = con.prepareStatement(putSalary);
            save = con.setSavepoint();

            stForInsertSalary.setInt(1, teacherID);
            stForInsertSalary.setDate(2, Date.valueOf(salary.getDateOfSalary()));
            stForInsertSalary.setInt(3, salary.getSalary());
            if (stForInsertSalary.executeUpdate() > 0) {
                log.info("Зарплата успешно добавлена");
                con.commit();
                return true;
            } else {
                log.error("Ошибка добавления");
                con.rollback(save);
                return false;
            }

        } catch (SQLException e) {
            log.error("Ошибка добавления: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForInsertSalary);
            closeResource(con);
        }
    }

    @Override
    public Salary getSalaryByID(int salaryID) {
        log.info("Попытка взять зарплату по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalaryByID)) {
            st.setInt(1, salaryID);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Найдена зарплата");
                return createSalaryFromSet(set);
            } else {
                log.error("Зарплата не найдена");
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
    public Set<Salary> getSalariesByTeacherId(int teacherId) {
        log.debug("Попытка взять зарплаты по ID учителя");
        Set<Salary> salaries = new HashSet<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalariesByTeacherID)) {
            st.setInt(1, teacherId);
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(createSalaryFromSet(set));
                log.info("Найдена зарплата");
            }
            return salaries;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return salaries;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public Set<Salary> getSalariesByDateOfSalary(LocalDate dateOfSalary) {
        log.debug("Попытка взять зарплаты по дате получения");
        Set<Salary> salaries = new HashSet<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalariesByDate)) {
            st.setDate(1, Date.valueOf(dateOfSalary));
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(createSalaryFromSet(set));
                log.info("Найдена зарплата");
            }
            return salaries;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return salaries;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public Set<Salary> getAllSalaries() {
        log.debug("Попытка взять все зарплаты");
        Set<Salary> salaries = new HashSet<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllSalaries)) {
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(createSalaryFromSet(set));
                log.info("Найдена зарплата");
            }
            return salaries;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return salaries;
        } finally {
            closeResource(set);
        }
    }

    @Override
    public boolean updateSalary(Salary newSalary) {
        if (updateSalaryCountByID(newSalary.getId(), newSalary.getSalary())) {
            log.info("Значение зарплаты обновлено");
        } else {
            log.error("Значение зарплаты не обновлено");
            return false;
        }

        if (updateDateOfSalaryById(newSalary.getId(), newSalary.getDateOfSalary())) {
            log.info("Дата получения зарплаты обновлена");
        } else {
            log.info("Дата получения зарплаты не обновлена");
        }

        log.info("Зарплата обновлена");
        return true;
    }

    private boolean updateSalaryCountByID(int id, int newSalaryCount) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateSalaryByID);
            save = con.setSavepoint();

            st.setInt(1, newSalaryCount);
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Зарплата успешно обновлена (обновлён размер зарплаты)");
                con.commit();
                return true;
            } else {
                log.error("Зарплата не обновлена (не обновлён размер зарплаты)");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Зарплата не найдена, изменения не произошло (не обновлён размер зарплаты)");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    private boolean updateDateOfSalaryById(int id, LocalDate newDateOfSalary) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateDateOfSalary);
            save = con.setSavepoint();

            st.setDate(1, Date.valueOf(newDateOfSalary));
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Зарплата успешно обновлена (обновлена дата получения)");
                con.commit();
                return true;
            } else {
                log.error("Зарплата не обновлена (не обновлена дата получения)");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Зарплата не найдена, изменения не произошло (не обновлена дата получения)");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    @Override
    public boolean deleteSalaryById(int id) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(deleteSalaryByID);
            save = con.setSavepoint();

            st.setInt(1, id);
            if (st.executeUpdate() > 0) {
                log.info("Зарплата успешно удалена");
                con.commit();
                return true;
            } else {
                log.error("Зарплата не удалена");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Зарплата не найдена, удаления не произошло");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(st);
            closeResource(con);
        }
    }

    // метод создания зарплаты через ResultSet

    public Salary createSalaryFromSet(ResultSet set) throws SQLException {
        return new Salary()
                .withId(set.getInt(1))
                .withDateOfSalary(set.getDate(2).toLocalDate())
                .withSalary(set.getInt(3));
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

