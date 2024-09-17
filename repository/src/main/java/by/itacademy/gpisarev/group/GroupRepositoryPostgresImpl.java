package by.itacademy.gpisarev.group;


import by.itacademy.gpisarev.credentials.Credentials;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.secondary.Mark;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Student;
import by.itacademy.gpisarev.users.Teacher;
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

import static by.itacademy.gpisarev.constants.Queries.deleteGroupById;
import static by.itacademy.gpisarev.constants.Queries.deleteStudentFromGroupByID;
import static by.itacademy.gpisarev.constants.Queries.deleteSubjectFromGroupByID;
import static by.itacademy.gpisarev.constants.Queries.findAllGroups;
import static by.itacademy.gpisarev.constants.Queries.findGroupByID;
import static by.itacademy.gpisarev.constants.Queries.findGroupByName;
import static by.itacademy.gpisarev.constants.Queries.findGroupWithStudentsByID;
import static by.itacademy.gpisarev.constants.Queries.findMarksByStudentID;
import static by.itacademy.gpisarev.constants.Queries.findPersonByID;
import static by.itacademy.gpisarev.constants.Queries.findPersonByName;
import static by.itacademy.gpisarev.constants.Queries.findSubjectByID;
import static by.itacademy.gpisarev.constants.Queries.findSubjectsByGroupID;
import static by.itacademy.gpisarev.constants.Queries.putGroup;
import static by.itacademy.gpisarev.constants.Queries.putStudentAndGroupID;
import static by.itacademy.gpisarev.constants.Queries.putSubjectAndGroupID;
import static by.itacademy.gpisarev.constants.Queries.updateGroupNameByID;
import static by.itacademy.gpisarev.constants.Queries.updateGroupTeacherByID;

@Slf4j
@Repository
public class GroupRepositoryPostgresImpl implements GroupRepository {
    private final ComboPooledDataSource pool;

    @Autowired
    public GroupRepositoryPostgresImpl(ComboPooledDataSource pool) {
        this.pool = pool;
    }

    @Override
    public boolean createGroup(Group group) {
        Connection con = null;
        PreparedStatement stForInsertGroup = null;
        PreparedStatement stForFindTeacherId = null;
        Savepoint save = null;

        if (!isGroupFind(group)) {
            try {
                con = pool.getConnection();
                con.setAutoCommit(false);
                stForInsertGroup = con.prepareStatement(putGroup);
                stForFindTeacherId = con.prepareStatement(findPersonByName);
                save = con.setSavepoint();

                if (group.getTeacher() != null) {
                    int teacherId = findTeacherId(group, stForFindTeacherId);
                    stForInsertGroup.setInt(1, teacherId);
                } else {
                    stForInsertGroup.setInt(1, 0);
                }

                stForInsertGroup.setString(2, group.getName());
                if (stForInsertGroup.executeUpdate() > 0) {
                    log.info("Группа добавлена");
                    con.commit();
                    return true;
                } else {
                    log.error("Группа не добавлена");
                    con.rollback(save);
                    return false;
                }
            } catch (SQLException e) {
                log.error("Ошибка получения: SQLException");
                myRollback(con, save);
                return false;
            } finally {
                closeResource(stForInsertGroup);
                closeResource(stForFindTeacherId);
                closeResource(con);
            }
        } else {
            log.error("Создания не произошло");
            return false;
        }
    }

    @Override
    public Group getGroupById(int id) {
        log.debug("Попытка найти группу по ID");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement stForFindGroupById = con.prepareStatement(findGroupByID);
             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID);
             PreparedStatement stForFindStudentById = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectById = con.prepareStatement(findSubjectByID))
        {
            stForFindGroupById.setInt(1, id);
            set = stForFindGroupById.executeQuery();
            if (set.next()) {
                log.info("Группа найдена");
                return new Group()
                        .withId(set.getInt(1))
                        .withName(set.getString(2))
                        .withTeacher(new Teacher()
                                .withId(set.getInt(3))
                                .withFirstName(set.getString(4))
                                .withLastName(set.getString(5))
                                .withPatronymic(set.getString(6))
                                .withDateOfBirth(set.getDate(7).toLocalDate())
                                .withCredentials(new Credentials()
                                        .withId(set.getInt(8))
                                        .withLogin(set.getString(9))
                                        .withPassword(set.getString(10))))
                        .withStudents(getAllStudents(stForFindAllStudents, set.getInt(1), stForFindStudentById))
                        .withSubjects(getAllSubjects(stForFindAllSubjects, set.getInt(1), stForFindSubjectById));
            } else {
                log.error("Группа не найдена");
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
    public Group getGroupByName(String name) {
        log.debug("Попытка найти группу по названию");
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findGroupByName);
             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID);
             PreparedStatement stForFindStudentById = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectById = con.prepareStatement(findSubjectByID)) {
            st.setString(1, name);
            set = st.executeQuery();
            if (set.next()) {
                log.info("Группа найдена");
                return new Group()
                        .withId(set.getInt(1))
                        .withName(set.getString(2))
                        .withTeacher(new Teacher()
                                .withId(set.getInt(3))
                                .withFirstName(set.getString(4))
                                .withLastName(set.getString(5))
                                .withPatronymic(set.getString(6))
                                .withDateOfBirth(set.getDate(7).toLocalDate())
                                .withCredentials(new Credentials()
                                        .withId(set.getInt(8))
                                        .withLogin(set.getString(9))
                                        .withPassword(set.getString(10))))
                        .withStudents(getAllStudents(stForFindAllStudents, set.getInt(1), stForFindStudentById))
                        .withSubjects(getAllSubjects(stForFindAllSubjects, set.getInt(1), stForFindSubjectById));
            } else {
                log.error("Группа не найдена");
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
    public Set<Group> getAllGroups() {
        log.debug("Попытка найти все группы");
        Set<Group> groups = new HashSet<>();
        ResultSet set = null;
        try (Connection con = pool.getConnection();
             PreparedStatement st = con.prepareStatement(findAllGroups);
             PreparedStatement stForFindAllStudents = con.prepareStatement(findGroupWithStudentsByID);
             PreparedStatement stForFindAllSubjects = con.prepareStatement(findSubjectsByGroupID);
             PreparedStatement stForFindStudentById = con.prepareStatement(findPersonByID);
             PreparedStatement stForFindSubjectById = con.prepareStatement(findSubjectByID)) {
            set = st.executeQuery();
            while (set.next()) {
                log.info("Группа найдена");
                groups.add(new Group()
                        .withId(set.getInt(1))
                        .withName(set.getString(2))
                        .withTeacher(new Teacher()
                                .withId(set.getInt(3))
                                .withFirstName(set.getString(4))
                                .withLastName(set.getString(5))
                                .withPatronymic(set.getString(6))
                                .withDateOfBirth(set.getDate(7).toLocalDate())
                                .withCredentials(new Credentials()
                                        .withId(set.getInt(8))
                                        .withLogin(set.getString(9))
                                        .withPassword(set.getString(10))))
                        .withStudents(getAllStudents(stForFindAllStudents, set.getInt(1), stForFindStudentById))
                        .withSubjects(getAllSubjects(stForFindAllSubjects, set.getInt(1), stForFindSubjectById)));
            }
            return groups;
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            return groups;
        } finally {
            closeResource(set);
        }
    }

    private Set<Subject> getAllSubjects(PreparedStatement stForFindAllSubjectsInGroup, int groupId, PreparedStatement stForFindSubjectById) throws SQLException {
        Set<Subject> subjects = new HashSet<>();

        stForFindAllSubjectsInGroup.setInt(1, groupId);
        ResultSet setForGroup = stForFindAllSubjectsInGroup.executeQuery();

        while (setForGroup.next()) {
            stForFindSubjectById.setInt(1, setForGroup.getInt(1));

            ResultSet setForSubjects = stForFindSubjectById.executeQuery();
            if (setForSubjects.next()) {
                subjects.add(new Subject()
                        .withId(setForSubjects.getInt(1))
                        .withName(setForSubjects.getString(2)));
            }
        }
        return subjects;
    }

    private Set<Student> getAllStudents(PreparedStatement stForFindAllStudentsInGroup, int groupId, PreparedStatement stForFindStudentById) throws SQLException {
        Set<Student> students = new HashSet<>();

        stForFindAllStudentsInGroup.setInt(1, groupId);
        ResultSet setForGroup = stForFindAllStudentsInGroup.executeQuery();
        ResultSet setForStudents;

        while (setForGroup.next()) {
            stForFindStudentById.setInt(1, setForGroup.getInt(1));

            setForStudents = stForFindStudentById.executeQuery();
            if (setForStudents.next()) {
                students.add(new Student()
                        .withId(setForStudents.getInt(1))
                        .withFirstName(setForStudents.getString(2))
                        .withLastName(setForStudents.getString(3))
                        .withPatronymic(setForStudents.getString(4))
                        .withDateOfBirth(setForStudents.getDate(5).toLocalDate())
                        .withCredentials(new Credentials()
                                .withId(setForStudents.getInt(7))
                                .withLogin(setForStudents.getString(8))
                                .withPassword(setForStudents.getString(9)))
                        .withMarks(findAllStudentMarks(setForStudents.getInt(1))));
            }

        }
        return students;
    }

    @Override
    public boolean updateGroup(Group newGroup) {
        String newName = newGroup.getName();
        if (updateGroupNameById(newGroup.getId(), newName)) {
            log.info("Название группы изменено");
        } else {
            log.error("Название группы не обновлено");
            return false;
        }

        Teacher newTeacher = newGroup.getTeacher();
        if (updateGroupTeacherById(newGroup.getId(), newTeacher)) {
            log.info("Учитель группы обновлён");
        } else {
            log.error("Учитель группы не обновлён");
            return false;
        }

        log.info("Группа обновлена");
        return true;
    }

    @Override
    public boolean updateStudentsAdd(Group group, Student newStudent) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(putStudentAndGroupID);
            save = con.setSavepoint();

            st.setInt(1, newStudent.getId());
            st.setInt(2, group.getId());

            if (st.executeUpdate() > 0) {
                log.info("Студент успешно добавлен к группе");
                con.commit();
                return true;
            } else {
                log.error("Студент не добавлен к группе");
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
    public boolean updateStudentsRemove(Group group, Student removableStudent) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(deleteStudentFromGroupByID);
            save = con.setSavepoint();

            st.setInt(1, removableStudent.getId());
            if (st.executeUpdate() > 0) {
                log.info("Студент успешно удалён из группы");
                con.commit();
                return true;
            } else {
                log.error("Студент не удалён из группы");
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
    public boolean updateSubjectsAdd(Group group, Subject newSubject) {
        Connection con = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForUpdate = con.prepareStatement(putSubjectAndGroupID);
            save = con.setSavepoint();


            stForUpdate.setInt(1, newSubject.getId());
            stForUpdate.setInt(2, group.getId());

            if (stForUpdate.executeUpdate() > 0) {
                log.info("Предмет успешно добавлен к группе");
                con.commit();
                return true;
            } else {
                log.error("Предмет не добавлен к группе");
                con.rollback(save);
                return false;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean updateSubjectsRemove(Group group, Subject removableSubject) {
        Connection con = null;
        PreparedStatement stForUpdate = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            stForUpdate = con.prepareStatement(deleteSubjectFromGroupByID);
            save = con.setSavepoint();

            stForUpdate.setInt(1, removableSubject.getId());
            if (stForUpdate.executeUpdate() > 0) {
                log.info("Предмет успешно удалён из группы");
                con.commit();
                return true;
            } else {
                log.error("Предмет не удалён из группы");
                con.rollback(save);
                return false;
            }


        } catch (SQLException e) {
            log.error("Ошибка получения: SQLException");
            myRollback(con, save);
            return false;
        } finally {
            closeResource(stForUpdate);
            closeResource(con);
        }
    }

    @Override
    public boolean deleteGroupById(int id) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;
        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(deleteGroupById);
            save = con.setSavepoint();

            if (isGroupHasDeleted(st, id)) {
                log.info("Группа полностью удалена, удаление завершено");
                con.commit();
                return true;
            } else {
                log.error("Группа не удалена, удаление прервано");
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

    // метод поиска всех оценок студента

    private Set<Mark> findAllStudentMarks(int studentID) {
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

    // метод удаления самой группы

    private boolean isGroupHasDeleted(PreparedStatement stForDeleteGroup, int groupId) throws SQLException {
        log.info("Попытка удалить группу");
        stForDeleteGroup.setInt(1, groupId);
        return stForDeleteGroup.executeUpdate() > 0;
    }

    // метод для поиска группы

    private boolean isGroupFind(Group group) {
        Group findableGroup = getGroupByName(group.getName());
        if (findableGroup != null) {
            log.error("Группа с таким названием уже существует");
            return true;
        } else {
            log.info("Группа не найдена, начинаем создание");
            return false;
        }
    }

    // метод обновления названия группы

    private boolean updateGroupNameById(int id, String newName) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateGroupNameByID);
            save = con.setSavepoint();

            st.setString(1, newName);
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                con.commit();
                return true;
            } else {
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

    // метод обновления учителя группы

    private boolean updateGroupTeacherById(int id, Person newTeacher) {
        Connection con = null;
        PreparedStatement st = null;
        Savepoint save = null;

        try {
            con = pool.getConnection();
            con.setAutoCommit(false);
            st = con.prepareStatement(updateGroupTeacherByID);
            save = con.setSavepoint();

            int teacherId = newTeacher.getId();
            st.setInt(1, teacherId);
            st.setInt(2, id);
            if (st.executeUpdate() > 0) {
                con.commit();
                return true;
            } else {
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

    // метод для поиска ID Teacher

    private int findTeacherId(Group group, PreparedStatement stForFindTeacherId) throws SQLException {
        stForFindTeacherId.setString(1, group.getTeacher().getFirstName());
        stForFindTeacherId.setString(2, group.getTeacher().getLastName());
        stForFindTeacherId.setString(3, group.getTeacher().getPatronymic());

        ResultSet setForTeacher = stForFindTeacherId.executeQuery();
        if (setForTeacher.next()) {
            return setForTeacher.getInt(1);
        } else {
            return 0;
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

