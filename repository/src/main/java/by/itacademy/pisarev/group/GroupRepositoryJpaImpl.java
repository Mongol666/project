package by.itacademy.pisarev.group;

import by.itacademy.pisarev.AbstractRepoJpa;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import role.Role;
import secondary.Group;
import secondary.Subject;
import users.Student;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Set;

@Slf4j
public class GroupRepositoryJpaImpl extends AbstractRepoJpa<Group> implements GroupRepository {

    private static volatile GroupRepositoryJpaImpl instance;

    private GroupRepositoryJpaImpl(SessionFactory factory) {
        super(factory, Group.class);
    }

    public static GroupRepositoryJpaImpl getInstance(SessionFactory factory) {
        if (instance == null) {
            synchronized (GroupRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new GroupRepositoryJpaImpl(factory);
                }
            }
        }
        return instance;
    }


    @Override
    public boolean createGroup(Group group) {
        return create(group);
    }

    @Override
    public Group getGroupById(int id) {
        return getByID(id);
    }

    @Override
    public Group getGroupByName(String name) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        Group result = null;
        try {
            transaction.begin();
            result = manager
                    .createNamedQuery("getGroupByName", Group.class)
                    .setParameter("name", name)
                    .getSingleResult();
            if (result != null) {
                log.info("Группа найдена по названию");
            } else {
                log.error("Не найдена группа по названию");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения группы по названию: " + e.getMessage());
            return result;
        } finally {
            manager.close();
        }
    }

    @Override
    public Set<Group> getAllGroups() {
        return getAll();
    }

    @Override
    public boolean updateGroup(Group newGroup) {
        return update(newGroup);
    }

    @Override
    public boolean updateStudentsAdd(Group group, Student newStudent) {
        if (newStudent.getRole() == Role.STUDENT) {
            group.addStudent(newStudent);
            return update(group);
        } else {
            log.error("{} не является студентом", newStudent);
            return false;
        }
    }

    @Override
    public boolean updateStudentsRemove(Group group, Student removableStudent) {
        boolean result = false;
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            result = manager.createNativeQuery("delete from group_student where group_id = ? and student_id = ?")
                    .setParameter(1, group.getId())
                    .setParameter(2, removableStudent.getId())
                    .executeUpdate() > 0;
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка обновления {} : {}", group.getClass().getName(), e.getMessage());
        } finally {
            manager.close();
        }
        return result;
    }

    @Override
    public boolean updateSubjectsAdd(Group group, Subject newSubject) {
        group.addSubject(newSubject);
        return update(group);
    }

    @Override
    public boolean updateSubjectsRemove(Group group, Subject removableSubject) {
        boolean result = false;
        EntityManager manager = getEntityManager();
        try {
            manager.getTransaction().begin();
            result = manager.createNativeQuery("delete from group_subject where group_id = ? and subject_id = ?")
                    .setParameter(1, group.getId())
                    .setParameter(2, removableSubject.getId())
                    .executeUpdate() > 0;
            manager.getTransaction().commit();
        } catch (Exception e) {
            log.error("Ошибка обновления {} : {}", group.getClass().getName(), e);
        } finally {
            manager.close();
        }

        return result;
    }

    @Override
    public boolean deleteGroupById(int id) {
        Group group = getGroupById(id);
        if (group != null) {
            return remove(group);
        } else {
            log.error("{} не найдена, удаления не произошло", Group.class.getName());
            return false;
        }
    }
}
