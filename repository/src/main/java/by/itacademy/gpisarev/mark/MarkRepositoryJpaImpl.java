package by.itacademy.gpisarev.mark;

import by.itacademy.gpisarev.AbstractRepoJpa;
import by.itacademy.gpisarev.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Mark;
import by.itacademy.gpisarev.users.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import by.itacademy.gpisarev.users.Student;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Repository
public class MarkRepositoryJpaImpl extends AbstractRepoJpa<Mark> implements MarkRepository {

    private static volatile PersonRepository personRepository;

    @Autowired
    public MarkRepositoryJpaImpl(SessionFactory factory,
                                 @Qualifier("personRepositoryJpaImpl") PersonRepository personRepo) {
        super(factory, Mark.class);
        personRepository = personRepo;
    }

    @Override
    public boolean createMark(Mark mark, int studentID) {
        Person person = personRepository.getPersonById(studentID);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                Student student = (Student) person;
                student.addMark(mark);
                return personRepository.updatePerson(student);
            } else {
                log.error("{} не является студентом", person);
                return false;
            }
        } else {
            log.error("Студент не найден");
            return false;
        }
    }

    @Override
    public Mark getMarkByID(int id) {
        return getByID(id);
    }

    @Override
    public Set<Mark> getAllMarks() {
        return getAll();
    }

    @Override
    public Set<Mark> getMarksByStudentID(int studentID) {
        Person person = personRepository.getPersonById(studentID);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                Student student = (Student) person;
                return student.getMarks();
            } else {
                log.error("{} не является студентом", person);
                return new HashSet<>();
            }
        } else {
            log.error("Студент не найден");
            return new HashSet<>();
        }
    }

    @Override
    public boolean updateMark(Mark newMark) {
        return update(newMark);
    }

    @Override
    public boolean deleteMarkById(int id) {
        Mark mark = getMarkByID(id);
        if (mark != null) {
            return remove(mark);
        } else {
            log.error("{} не найдена, удаления не произошло", Mark.class.getName());
            return false;
        }
    }
}
