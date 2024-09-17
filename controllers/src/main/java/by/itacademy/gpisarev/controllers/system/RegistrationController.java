package by.itacademy.gpisarev.controllers.system;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.credentials.Credentials;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.users.Student;
import by.itacademy.gpisarev.users.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("registration")
public class RegistrationController extends AbstractController {

    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile PersonRepository personRepository;

    @Autowired
    public RegistrationController(Map<String, PersonRepository> personRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @PostMapping
    public ModelAndView doPost(@RequestParam("firstName") String firstName,
                                  @RequestParam("lastName") String lastName,
                                  @RequestParam("patronymic") String patronymic,
                                  @RequestParam("dateOfBirth") String dateOfBirth,
                                  @RequestParam("role") String role,
                                  @RequestParam("login") String login,
                                  @RequestParam("password") String password) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("Получение данных о новом пользователе");

        switch (role) {
            case "Студент":
                if (personRepository.createPerson(new Student()
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withPatronymic(patronymic)
                        .withDateOfBirth(LocalDate.parse(dateOfBirth))
                        .withCredentials(new Credentials()
                                .withLogin(login)
                                .withPassword(password)))) {
                    log.info("Студент успешно добавлен");
                    modelAndView.setViewName("/index");
                    modelAndView.getModel().put("messageAboutCreate", "Регистрация прошла успешно!");
                } else {
                    log.error("Студент не добавлен");
                    modelAndView.setViewName("/index");
                    modelAndView.getModel().put("errorMessage", "Регистрация прошла неуспешно!");
                }
                break;
            case "Учитель":
                if (personRepository.createPerson(new Teacher()
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withPatronymic(patronymic)
                        .withDateOfBirth(LocalDate.parse(dateOfBirth))
                        .withCredentials(new Credentials()
                                .withLogin(login)
                                .withPassword(password)))) {
                    log.info("Учитель успешно добавлен");
                    modelAndView.setViewName("/index");
                    modelAndView.getModel().put("messageAboutCreate", "Регистрация прошла успешно!");
                } else {
                    log.error("Учитель не добавлен");
                    modelAndView.setViewName("/index");
                    modelAndView.getModel().put("errorMessage", "Регистрация прошла неуспешно!");
                }
                break;
        }
        return modelAndView;
    }
}
