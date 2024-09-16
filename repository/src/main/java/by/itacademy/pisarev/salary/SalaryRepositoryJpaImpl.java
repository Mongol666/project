package by.itacademy.pisarev.salary;

import by.itacademy.pisarev.AbstractRepoJpa;
import by.itacademy.pisarev.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import role.Role;
import secondary.Salary;
import users.Person;
import users.Teacher;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class SalaryRepositoryJpaImpl extends AbstractRepoJpa<Salary> implements SalaryRepository {

    private static volatile PersonRepository personRepository;

    private static volatile SalaryRepositoryJpaImpl instance;

    private SalaryRepositoryJpaImpl(SessionFactory factory, PersonRepository personRepo) {
        super(factory, Salary.class);
        personRepository = personRepo;
    }
    public static SalaryRepositoryJpaImpl getInstance(SessionFactory factory, PersonRepository personRepo) {
        if (instance == null) {
            synchronized (SalaryRepositoryJpaImpl.class) {
                if (instance == null) {
                    instance = new SalaryRepositoryJpaImpl(factory, personRepo);
                }
            }
        }
        return instance;
    }

    @Override
    public boolean createSalary(Salary salary, int teacherID) {
        Person person = personRepository.getPersonById(teacherID);
        if (person != null) {
            if (person.getRole() == Role.TEACHER) {
                Teacher teacher = (Teacher) person;
                teacher.addSalary(salary);
                return personRepository.updatePerson(teacher);
            } else {
                log.error("{} не является учителем", person);
                return false;
            }
        } else {
            log.error("Учитель не найден");
            return false;
        }
    }

    @Override
    public Salary getSalaryByID(int salaryID) {
        return getByID(salaryID);
    }

    @Override
    public Set<Salary> getSalariesByTeacherId(int teacherID) {
        Person person = personRepository.getPersonById(teacherID);
        if (person != null) {
            if (person.getRole() == Role.TEACHER) {
                Teacher teacher = (Teacher) person;
                return teacher.getSalaries();
            } else {
                log.error("{} не является учителем", person);
                return new HashSet<>();
            }
        } else {
            log.error("Учитель не найден");
            return new HashSet<>();
        }
    }

    @Override
    public Set<Salary> getSalariesByDateOfSalary(LocalDate dateOfSalary) {
        EntityManager manager = getEntityManager();
        EntityTransaction transaction = manager.getTransaction();
        Set<Salary> result = new HashSet<>();
        try {
            transaction.begin();
            TypedQuery<Salary> query = manager
                    .createNamedQuery("getSalariesByDate", Salary.class)
                    .setParameter("dateOfSalary", dateOfSalary);
            result = new HashSet<>(query.getResultList());
            if (!result.isEmpty()) {
                log.info("Зарплаты найдены по дате");
            } else {
                log.error("Зарплаты не найдены по дате");
            }
            transaction.commit();
            return result;
        } catch (Exception e) {
            log.error("Ошибка нахождения зарплат по дате: " + e.getMessage());
        } finally {
            manager.close();
        }
        return result;
    }

    @Override
    public Set<Salary> getAllSalaries() {
        return getAll();
    }

    @Override
    public boolean updateSalary(Salary salary) {
        return update(salary);
    }

    @Override
    public boolean deleteSalaryById(int id) {
        Salary salary = getSalaryByID(id);
        if (salary != null) {
            return remove(salary);
        } else {
            log.error("{} не найдена, удаления не произошло", Salary.class.getName());
            return false;
        }
    }
}
