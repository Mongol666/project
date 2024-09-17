package by.itacademy.gpisarev.rest_controllers;

import by.itacademy.gpisarev.entity.AbstractEntity;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.salary.SalaryRepository;
import by.itacademy.gpisarev.secondary.Salary;
import by.itacademy.gpisarev.users.Person;
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

@RestController
@RequestMapping("/rest/teachers/{teacherID}/salaries")
public class SalaryRestController extends AbstractRestController {
    private static final String PERSON_REPO_PREFIX = "personRepository";
    private static final String SALARY_REPO_PREFIX = "salaryRepository";

    private final Map<String, PersonRepository> personRepositoryMap;
    private final Map<String, SalaryRepository> salaryRepositoryMap;

    private volatile PersonRepository personRepository;
    private volatile SalaryRepository salaryRepository;

    @Autowired
    public SalaryRestController(Map<String, PersonRepository> personRepositoryMap,
                            Map<String, SalaryRepository> salaryRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
        this.salaryRepositoryMap = salaryRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        salaryRepository = salaryRepositoryMap.get(SALARY_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public Set<Salary> getAllTeacherSalaries(@PathVariable("teacherID") int teacherID) {
        return salaryRepository.getSalariesByTeacherId(teacherID);
    }

    @GetMapping("/{salaryID}")
    public Salary getSalaryByID(@PathVariable("teacherID") int teacherID, @PathVariable("salaryID") int salaryID) {
        Person person = personRepository.getPersonById(teacherID);
        Salary salary = salaryRepository.getSalaryByID(salaryID);
        if (person != null && salary != null && person.getRole() == Role.TEACHER) {
            return salary;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Зарплата по ID " + salaryID + " не найдена");
        }
    }

    @PostMapping
    public Salary addSalary(@PathVariable("teacherID") int teacherID, @RequestBody Salary newSalary) {
        if (salaryRepository.createSalary(newSalary, teacherID)) {
            return Collections.max(salaryRepository.getAllSalaries(), Comparator.comparing(AbstractEntity::getId));
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Зарплата не добавлена");
        }
    }

    @PutMapping("/{salaryID}")
    public Salary updateSalary(@PathVariable("salaryID") int salaryID, @PathVariable("teacherID") int teacherID,
                               @RequestBody Salary newSalary) {
        Salary oldSalary = salaryRepository.getSalaryByID(salaryID);
        Person person = personRepository.getPersonById(teacherID);
        if (oldSalary != null && person != null && person.getRole() == Role.TEACHER) {
            newSalary.setId(salaryID);
            if (salaryRepository.updateSalary(newSalary)) {
                return oldSalary;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Зарплата не обновлена");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Зарплата не обновлена");
        }
    }

    @DeleteMapping("/{salaryID}")
    public Salary deleteSalary(@PathVariable("salaryID") int salaryID, @PathVariable("teacherID") int teacherID) {
        Salary salary = salaryRepository.getSalaryByID(salaryID);
        Person person = personRepository.getPersonById(teacherID);

        if (salary != null && person != null && person.getRole() == Role.TEACHER) {
            if (salaryRepository.deleteSalaryById(salaryID)) {
                return salary;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Зарплата не удалена");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Зарплата не удалена");
        }

    }
}
