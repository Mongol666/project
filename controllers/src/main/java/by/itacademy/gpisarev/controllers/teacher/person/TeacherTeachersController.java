package by.itacademy.gpisarev.controllers.teacher.person;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.users.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("teacher/teachers")
public class TeacherTeachersController extends AbstractController {

    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile PersonRepository personRepository;

    @Autowired
    public TeacherTeachersController(Map<String, PersonRepository> personRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public ModelAndView get() {
        ModelAndView modelAndView = new ModelAndView("/teacher_teachers");
        Set<Person> teachers = personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.TEACHER)
                .collect(Collectors.toSet());
        modelAndView.getModel().put("allTeachers", teachers);
        modelAndView.getModel().put("messageFromTeachers", "Все учителя");
        return modelAndView;
    }
}
