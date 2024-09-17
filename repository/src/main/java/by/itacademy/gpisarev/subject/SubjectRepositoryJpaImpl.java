package by.itacademy.gpisarev.subject;

import by.itacademy.gpisarev.AbstractRepoJpa;
import by.itacademy.gpisarev.group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.secondary.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Repository
public class SubjectRepositoryJpaImpl extends AbstractRepoJpa<Subject> implements SubjectRepository {

    private static GroupRepository groupRepository;

    @Autowired
    private SubjectRepositoryJpaImpl(SessionFactory factory,
                                     @Qualifier("groupRepositoryJpaImpl") GroupRepository groupRepo) {
        super(factory, Subject.class);
        groupRepository = groupRepo;
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
