package by.itacademy.gpisarev.rest_controllers;

import by.itacademy.gpisarev.entity.AbstractEntity;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Teacher;
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
@RequestMapping("/rest/teachers")
public class TeacherRestController extends AbstractRestController {

    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile PersonRepository personRepository;

    @Autowired
    public TeacherRestController(Map<String, PersonRepository> personRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public Set<Person> getAllTeachers() {
        return personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.TEACHER)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public Teacher getStudentByID(@PathVariable("id") int id) {
        Person person = personRepository.getPersonById(id);
        if (person != null && person.getRole() == Role.TEACHER) {
            return (Teacher) person;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Учитель по ID " + id + " не найден");
        }
    }

    @PostMapping
    public Teacher addStudent(@RequestBody Teacher newTeacher) {
        if (personRepository.createPerson(newTeacher)) {
            Set<Person> teachers = personRepository.getAllPersons()
                    .stream()
                    .filter(person -> person.getRole() == Role.TEACHER)
                    .collect(Collectors.toSet());
            return (Teacher) Collections.max(teachers, Comparator.comparing(AbstractEntity::getId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Учитель не создан");
        }
    }

    @PutMapping("/{id}")
    public Teacher updateTeacher(@RequestBody Teacher updatedTeacher, @PathVariable("id") int id) {
        Person oldTeacher = personRepository.getPersonById(id);
        if (oldTeacher != null && oldTeacher.getRole() == Role.TEACHER) {
            updatedTeacher.setId(id);
            if (personRepository.updateAllPersonProperties(updatedTeacher)) {
                return (Teacher) oldTeacher;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Учитель не обновлён");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Учитель не обновлён");
        }
    }

    @DeleteMapping("/{id}")
    public Teacher deleteStudent(@PathVariable("id") int id) {
        Person deletableTeacher = personRepository.getPersonById(id);
        if (deletableTeacher != null && deletableTeacher.getRole() == Role.TEACHER) {
            if (personRepository.deletePersonById(id)) {
                return (Teacher) deletableTeacher;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Учитель не удалён");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Учитель не удалён");
        }
    }
}
