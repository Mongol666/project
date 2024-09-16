package by.itacademy.pisarev.person;

import by.itacademy.pisarev.AbstractRepoJpa;
import by.itacademy.pisarev.group.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import role.Role;
import secondary.Group;
import secondary.Mark;
import secondary.Salary;
import users.Person;
import users.Student;
import users.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class PersonRepositoryJpaImpl extends AbstractRepoJpa<Person> implements PersonRepository {

    private static volatile GroupRepository groupRepository;

    private static volatile PersonRepositoryJpaImpl instance;

    private PersonRepositoryJpaImpl(SessionFactory factory, GroupRepository groupRepo) {
        super(factory, Person.class);
        groupRepository = groupRepo;
    }
    public static PersonRepositoryJpaImpl getInstance(SessionFactory factory, GroupRepository groupRepo) {
        if (instance == null) {
            synchronized (PersonRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new PersonRepositoryJpaImpl(factory, groupRepo);
                }
            }
        }
        return instance;
    }

    @Override
    public boolean createPerson(Person person) {
        return create(person);
    }

    @Override
    public Person getPersonById(int id) {
        return getByID(id);
    }

    @Override
    public Person getPersonByName(String firstName, String lastName, String patronymic) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        Person result = null;
        try {
            transaction.begin();
            result = manager
                    .createNamedQuery("getPersonByNames", Person.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("patronymic", patronymic)
                    .getSingleResult();
            if (result != null) {
                log.info("Пользователь найден по ФИО");
            } else {
                log.error("Пользователь не найден по ФИО");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения пользователя по ФИО: " + e.getMessage());
        } finally {
            manager.close();
        }
        return result;
    }

    @Override
    public Person getPersonByCredentials(String login, String password) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        Person result = null;
        try {
            transaction.begin();
            result = manager
                    .createNamedQuery("getPersonByCredentials", Person.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
            if (result != null) {
                log.info("Пользователь найден по логину и паролю");
            } else {
                log.error("Пользователь не найден по логину и паролю");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения пользователя по логину и паролю: " + e.getMessage());
        } finally {
            manager.close();
        }
        return result;
    }

    @Override
    public Set<Student> getStudentsByGroupID(int groupID) {
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            return group.getStudents();
        } else {
            log.error("{} не найдена", Group.class.getName());
            return new HashSet<>();
        }
    }

    @Override
    public Set<Person> getAllPersons() {
        return getAll();
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
        return update(person);
    }

    @Override
    public boolean deletePersonById(int id) {
        Person person = getPersonById(id);
        if (person != null) {
            return remove(person);
        } else {
            log.error("{} не найден, удаления не произошло", Person.class.getName());
            return false;
        }
    }

    @Override
    public boolean deletePersonByName(String firstName, String lastName, String patronymic) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        boolean result = false;
        try {
            transaction.begin();
            Person personForDelete = manager
                    .createNamedQuery("getPersonByNames", Person.class)
                    .setParameter("firstName", firstName)
                    .setParameter("lastName", lastName)
                    .setParameter("patronymic", patronymic)
                    .getSingleResult();
            manager.remove(personForDelete);
            result = !manager.contains(personForDelete);
            if (result) {
                log.info("Пользователь удалён по ФИО");
                transaction.commit();
            } else {
                log.error("Пользователь не удалён по ФИО");
                transaction.rollback();
            }
            return result;
        } catch (Exception e) {
            log.error("Ошибка удаления пользователя по ФИО: " + e.getMessage());
            if (transaction.isActive()) {
                transaction.rollback();
            }
        } finally {
            manager.close();
        }
        return result;
    }
}
