package by.itacademy.gpisarev.person;


import by.itacademy.gpisarev.credentials.Credentials;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Mark;
import by.itacademy.gpisarev.secondary.Salary;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.users.Admin;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Student;
import by.itacademy.gpisarev.users.Teacher;
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

import static by.itacademy.gpisarev.constants.Queries.deleteCredentialsByLoginAndPassword;
import static by.itacademy.gpisarev.constants.Queries.deletePersonByID;
import static by.itacademy.gpisarev.constants.Queries.findAllPersons;
import static by.itacademy.gpisarev.constants.Queries.findCredentialsByLoginAndPassword;
import static by.itacademy.gpisarev.constants.Queries.findMarksByStudentID;
import static by.itacademy.gpisarev.constants.Queries.findPersonByCredentials;
import static by.itacademy.gpisarev.constants.Queries.findPersonByID;
import static by.itacademy.gpisarev.constants.Queries.findPersonByName;
import static by.itacademy.gpisarev.constants.Queries.findSalariesByTeacherID;
import static by.itacademy.gpisarev.constants.Queries.findSubjectByID;
import static by.itacademy.gpisarev.constants.Queries.putCredentials;
import static by.itacademy.gpisarev.constants.Queries.putPerson;
import static by.itacademy.gpisarev.constants.Queries.updatePersonCredentialsByID;
import static by.itacademy.gpisarev.constants.Queries.updatePersonDateOfBirthByID;
import static by.itacademy.gpisarev.constants.Queries.updatePersonNameByID;

@Slf4j
@Repository
public class PersonRepositoryPostgresImpl implements PersonRepository {
    private final ComboPooledDataSource pool;

    @Autowired
    public PersonRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    @Override
    public boolean createPerson(Person person) {
        log.debug("Попытка создать пользователя");
        Connection con = null;
        PreparedStatement stForInsertCred = null;
        PreparedStatement stForInsertPerson = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            stForInsertCred = con.prepareStatement(putCredentials);
            stForInsertPerson = con.prepareStatement(putPerson);
            con.setAutoCommit(false);
            save = con.setSavepoint();

            stForInsertCred.setString(1, person.getCredentials().getLogin());
            stForInsertCred.setString(2, person.getCredentials().getPassword());
            if (stForInsertCred.executeUpdate() > 0) {
                log.debug("Учётные данные созданы");
                con.commit();
                if (isInsertPerson(stForInsertPerson, person)) {
                    log.info("Пользователь успешно добавлен");
                    con.commit();
                    return true;
                } else {
                    log.error("Ошибка создания пользователя");
                    con.rollback(save);
                    return false;
                }
            } else {
                log.error("Ошибка создания учётных данных");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForInsertCred);
            closeResource(stForInsertPerson);
            closeResource(con);
        }
    }

    @Override
    public Person getPersonById(int id) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findPersonByID)) {
            st.setInt(1, id);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(set);
            } else {
                log.error("Пользователь не найден");
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
    public Person getPersonByName(String firstName, String lastName, String patronymic) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findPersonByName)) {
            st.setString(1, firstName);
            st.setString(2, lastName);
            st.setString(3, patronymic);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(set);
            } else {
                log.error("Пользователь не найден");
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
    public Person getPersonByCredentials(String login, String password) {
        log.debug("Попытка найти пользователя в репозитории");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findPersonByCredentials)) {
            st.setString(1, login);
            st.setString(2, password);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Пользователь найден");
                return findRoleAndReturnPerson(set);
            } else {
                log.error("Не найдены учётные данные пользователя, поиск прекращён");
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
    public Set<Student> getStudentsByGroupID(int groupID) {
        return null;
    }

    @Override
    public Set<Person> getAllPersons() {
        Set<Person> persons = new HashSet<>();
        log.info("Берём всех пользователей из репозитория");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllPersons)) {
            set = st.executeQuery();
            while (set.next()) {
                Person person = findRoleAndReturnPerson(set);
                if (person != null) {
                    persons.add(person);
                } else {
                    log.error("Пользователь не найден");
                }
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
        } finally {
            closeResource(set);
        }
        return persons;
    }

    @Override
    public boolean updateAllPersonProperties(Person newPerson) {
        switch (newPerson.getRole()) {
            case STUDENT:
                return updateStudent(newPerson);
            case TEACHER:
                return updateTeacher(newPerson);
        }
        return false;
    }

    private boolean updateTeacher(Person newPerson) {
        Teacher newTeacher = (Teacher) newPerson;
        Person person = getPersonById(newPerson.getId());
        if (person != null) {
            if (person.getRole() == Role.TEACHER) {
                Teacher teacher = (Teacher) person;
                Set<Salary> salaries = teacher.getSalaries();
                newTeacher.setSalaries(salaries);
                return updatePerson(newTeacher);
            } else {
                log.error("{} не является учителем", newPerson);
                return false;
            }
        } else {
            log.error("Учитель не найден, обновления не произошло");
            return false;
        }
    }

    private boolean updateStudent(Person newPerson) {
        Student newStudent = (Student) newPerson;
        Person person = getPersonById(newPerson.getId());
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                Student student = (Student) person;
                Set<Mark> marks = student.getMarks();
                newStudent.setMarks(marks);
                return updatePerson(newStudent);
            } else {
                log.error("{} не является студентом", newPerson);
                return false;
            }
        } else {
            log.error("Студент не найден, обновления не произошло");
            return false;
        }
    }

    @Override
    public boolean updatePerson(Person person) {
        return updatePersonNames(person.getId(), person.getFirstName(), person.getLastName(), person.getPatronymic()) &&
                updateDateOfBirthById(person.getId(), person.getDateOfBirth()) &&
                updateCredentialByPersonId(person.getId(), person.getCredentials());
    }

    private boolean updatePersonNames(int id, String newFirstName, String newLastName, String newPatronymic) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updatePersonNameByID);
            save = con.setSavepoint();

            st.setString(1, newFirstName);
            st.setString(2, newLastName);
            st.setString(3, newPatronymic);
            st.setInt(4, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение ФИО пользователя");
                con.commit();
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
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

    private boolean updateDateOfBirthById(int id, LocalDate newDateOfBirth) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updatePersonDateOfBirthByID);
            save = con.setSavepoint();

            st.setDate(1, Date.valueOf(newDateOfBirth));
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение даты рождения пользователя");
                con.commit();
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
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

    private boolean updateCredentialByPersonId(int id, Credentials newCredential) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updatePersonCredentialsByID);
            save = con.setSavepoint();

            st.setString(1, newCredential.getLogin());
            st.setString(2, newCredential.getPassword());
            st.setInt(3, id);
            if (st.executeUpdate() > 0) {
                log.info("Изменение учётных данных пользователя");
                con.commit();
                return true;
            } else {
                log.error("Пользователь не найден, изменений не произошло");
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
    public boolean deletePersonById(int id) {
        Person person = getPersonById(id);
        if (person == null) {
            log.error("Пользователь не удалён");
            return false;
        }

        return deletePerson(person);
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        Person person = getPersonByName(firstName, lastName, patronymic);
        if (person == null) {
            log.error("Пользователь не удалён");
            return false;
        }

        return deletePerson(person);
    }

    // общая часть удаления пользователя

    private boolean deletePerson(Person person) {
        Connection con = null;
        PreparedStatement stForDeletePerson = null;
        PreparedStatement stForDeleteCred = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            stForDeletePerson = con.prepareStatement(deletePersonByID);
            stForDeleteCred = con.prepareStatement(deleteCredentialsByLoginAndPassword);

            con.setAutoCommit(false);
            save = con.setSavepoint();

            log.info("Начинается удаление пользователя");

            if (isPersonDeleted(stForDeletePerson, person)) {
                log.info("Пользователь удалён");
                con.commit();
            } else {
                log.error("Пользователь не удалён, удаления не произошло");
                con.rollback(save);
                return false;
            }

            if (isCredentialsDeleted(stForDeleteCred, person)) {
                log.info("Учётные данные удалены, пользователь полностью удалён");
                con.commit();
                return true;
            } else {
                log.error("Учётные данные пользователя не удалены, удаления не произошло");
                con.rollback(save);
                return false;
            }

        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForDeletePerson);
            closeResource(stForDeleteCred);
            closeResource(con);
        }
    }

    // метод по удалению самого Person

    private boolean isPersonDeleted(PreparedStatement stForDeletePerson, Person person) throws SQLException {
        log.info("Попытка удаления пользователя из репозитория");
        stForDeletePerson.setInt(1, person.getId());
        return stForDeletePerson.executeUpdate() > 0;
    }

    // метод по удалению Credentials пользователя

    private boolean isCredentialsDeleted(PreparedStatement stForDeleteCred, Person person) throws SQLException {
        log.info("Попытка удаления учётных данных пользователя из репозитория");
        stForDeleteCred.setString(1, person.getCredentials().getLogin());
        stForDeleteCred.setString(2, person.getCredentials().getPassword());
        return stForDeleteCred.executeUpdate() > 0;
    }

    // метод для поиска зарплат по ID учителя (взят из SalaryRepositoryPostgresImpl)

    private Set<Salary> findAllTeacherSalaries(int teacherID) {
        Set<Salary> salaries = new HashSet<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findSalariesByTeacherID)) {
            st.setInt(1, teacherID);
            set = st.executeQuery();
            while (set.next()) {
                salaries.add(new Salary()
                        .withId(set.getInt(1))
                        .withDateOfSalary(set.getDate(2).toLocalDate())
                        .withSalary(set.getInt(3)));
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

    // метод поиска оценок по ID студента (взят из MarkRepositoryPostgresImpl)

    private Set<Mark> findAllStudentMarks(int studentID) {
        log.debug("Попытка получения всех оценок студента №" + studentID);
        Set<Mark> marks = new HashSet<>();
        ResultSet setForMark = null;
        ResultSet setForSubject = null;

        try (Connection con = pool.getConnection();
             PreparedStatement stForAllStudentMarks = con.prepareStatement(findMarksByStudentID);
             PreparedStatement stForFindSubjectToMark = con.prepareStatement(findSubjectByID)) {
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

                            .withSubject(new Subject()
                                    .withId(setForSubject.getInt(1))
                                    .withName(setForSubject.getString(2)))

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

    // методы, возвращающие пользователя в зависимости от его роли

    private Person findRoleAndReturnPerson(ResultSet setForPerson) throws SQLException {
        switch (setForPerson.getString("role")) {
            case "Админ":
                log.info("Пользователь - Admin");
                return returnAdmin(setForPerson);
            case "Учитель":
                log.info("Пользователь - Teacher");
                return returnTeacher(setForPerson);
            case "Студент":
                log.info("Пользователь - Student");
                return returnStudent(setForPerson);
        }
        return null;
    }

    private Admin returnAdmin(ResultSet setForPerson) throws SQLException {
        return new Admin()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(setForPerson.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForPerson.getInt(7))
                        .withLogin(setForPerson.getString(8))
                        .withPassword(setForPerson.getString(9)));
    }

    private Teacher returnTeacher(ResultSet setForPerson) throws SQLException {
        return new Teacher()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(setForPerson.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForPerson.getInt(7))
                        .withLogin(setForPerson.getString(8))
                        .withPassword(setForPerson.getString(9)))
                .withSalaries(findAllTeacherSalaries(setForPerson.getInt(1)));
    }

    private Student returnStudent(ResultSet setForPerson) throws SQLException {
        return new Student()
                .withId(setForPerson.getInt(1))
                .withFirstName(setForPerson.getString(2))
                .withLastName(setForPerson.getString(3))
                .withPatronymic(setForPerson.getString(4))
                .withDateOfBirth(setForPerson.getDate(5).toLocalDate())
                .withCredentials(new Credentials()
                        .withId(setForPerson.getInt(7))
                        .withLogin(setForPerson.getString(8))
                        .withPassword(setForPerson.getString(9)))
                .withMarks(findAllStudentMarks(setForPerson.getInt(1)));
    }

    // метод для вставки пользователя

    private boolean isInsertPerson(PreparedStatement st, Person person) throws SQLException {
        st.setString(1, person.getFirstName());
        st.setString(2, person.getLastName());
        st.setString(3, person.getPatronymic());
        st.setDate(4, Date.valueOf(person.getDateOfBirth()));

        int credId = getCredentialID(person.getCredentials().getLogin(), person.getCredentials().getPassword());
        if (credId != 0) {
            st.setInt(5, credId);
        } else {
            log.error("Учётные данные не найдены");
            return false;
        }

        st.setString(6, person.getRole().getRoleString());
        return st.executeUpdate() > 0;
    }

    // метод для поиска ID у Credentials

    private int getCredentialID(String login, String password) {
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findCredentialsByLoginAndPassword)) {
            st.setString(1, login);
            st.setString(2, password);
            set = st.executeQuery();
            if (set.next()) {
                return set.getInt(1);
            } else {
                log.error("Учётные данные не найдены");
                return 0;
            }
        } catch (SQLException e) {
            log.error("Учётные данные не найдены");
            return 0;
        } finally {
            closeResource(set);
        }
    }

    // Rollback

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