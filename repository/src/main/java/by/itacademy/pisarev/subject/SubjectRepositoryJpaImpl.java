package by.itacademy.pisarev.subject;

import by.itacademy.pisarev.AbstractRepoJpa;
import by.itacademy.pisarev.group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import secondary.Group;
import secondary.Subject;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class SubjectRepositoryJpaImpl extends AbstractRepoJpa<Subject> implements SubjectRepository {

    private static GroupRepository groupRepository;

    private static volatile SubjectRepositoryJpaImpl instance;

    private SubjectRepositoryJpaImpl(SessionFactory factory, GroupRepository groupRepo) {
        super(factory, Subject.class);
        groupRepository = groupRepo;

    }
    public static SubjectRepositoryJpaImpl getInstance(SessionFactory factory, GroupRepository groupRepo) {
        if (instance == null) {
            synchronized (SubjectRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new SubjectRepositoryJpaImpl(factory, groupRepo);
                }
            }
        }
        return instance;
    }

    @Override
    public boolean createSubject(Subject subject) {
        return create(subject);
    }

    @Override
    public Subject getSubjectById(int id) {
        return getByID(id);
    }

    @Override
    public Subject getSubjectByName(String name) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        Subject result = null;
        try {
            transaction.begin();
            result = manager
                    .createNamedQuery("getSubjectByName", Subject.class)
                    .setParameter("name", name)
                    .getSingleResult();
            if (result != null) {
                log.info("Предмет найден по названию");
            } else {
                log.error("Предмет не найден по названию");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения предмета по названию: " + e.getMessage());
        } finally {
            manager.close();
        }
        return result;
    }

    @Override
    public Set<Subject> getSubjectsByGroupID(int groupID) {
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            return group.getSubjects();
        } else {
            log.error("{} не найдена", Group.class.getName());
            return new HashSet<>();
        }
    }

    @Override
    public Set<Subject> getAllSubjects() {
        return getAll();
    }

    @Override
    public boolean updateSubject(Subject newSubject) {
       return update(newSubject);
    }

    @Override
    public boolean deleteSubjectById(int id) {
        Subject subject = getSubjectById(id);
        if (subject != null) {
            return remove(subject);
        } else {
            log.error("{} не найден, удаления не произошло", Subject.class.getName());
            return false;
        }
    }
}
