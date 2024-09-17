package by.itacademy.gpisarev.rest_controllers;

import by.itacademy.gpisarev.entity.AbstractEntity;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/students")
public class StudentRestController extends AbstractRestController {
    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile PersonRepository personRepository;

    @Autowired
    public StudentRestController(Map<String, PersonRepository> personRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public Set<Person> getAllStudents() {
        return personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.STUDENT)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public Student getStudentByID(@PathVariable("id") int id) {
        Person person = personRepository.getPersonById(id);
        if (person != null && person.getRole() == Role.STUDENT) {
            return (Student) person;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Студент по ID " + id + " не найден");
        }
    }

    @PostMapping
    public Student addStudent(@RequestBody Student newStudent) {
        if (personRepository.createPerson(newStudent)) {
            Set<Person> students = personRepository.getAllPersons()
                    .stream()
                    .filter(person -> person.getRole() == Role.STUDENT)
                    .collect(Collectors.toSet());
            return (Student) Collections.max(students, Comparator.comparing(AbstractEntity::getId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Студент не создан");
        }
    }

    @PutMapping("/{id}")
    public Student updateStudent(@RequestBody Student updatedStudent, @PathVariable("id") int id) {
        Person oldStudent = personRepository.getPersonById(id);
        if (oldStudent != null && oldStudent.getRole() == Role.STUDENT) {
            updatedStudent.setId(id);
            if (personRepository.updateAllPersonProperties(updatedStudent)) {
                return (Student) oldStudent;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Студент не обновлён");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Студент не обновлён");
        }
    }

    @DeleteMapping("/{id}")
    public Student deleteStudent(@PathVariable("id") int id) {
        Person deletableStudent = personRepository.getPersonById(id);
        if (deletableStudent != null && deletableStudent.getRole() == Role.STUDENT) {
            if (personRepository.deletePersonById(id)) {
                return (Student) deletableStudent;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Студент не удалён");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Студент не удалён");
        }
    }
}
