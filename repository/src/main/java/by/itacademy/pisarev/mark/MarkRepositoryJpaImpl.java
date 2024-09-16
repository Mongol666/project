package by.itacademy.pisarev.mark;

import by.itacademy.pisarev.AbstractRepoJpa;
import by.itacademy.pisarev.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import role.Role;
import secondary.Mark;
import users.Person;
import users.Student;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class MarkRepositoryJpaImpl extends AbstractRepoJpa<Mark> implements MarkRepository {

    private static volatile PersonRepository personRepository;

    private static volatile MarkRepositoryJpaImpl instance;

    private MarkRepositoryJpaImpl(SessionFactory factory, PersonRepository personRepo) {
        super(factory, Mark.class);
        personRepository = personRepo;
    }
    public static MarkRepositoryJpaImpl getInstance(SessionFactory factory, PersonRepository personRepo) {
        if (instance == null) {
            synchronized (MarkRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new MarkRepositoryJpaImpl(factory, personRepo);
                }
            }
        }
        return instance;
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
