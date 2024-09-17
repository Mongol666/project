package by.itacademy.gpisarev.controllers.admin.person;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.credentials.Credentials;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("admin/teachers")
public class AdminTeacherController extends AbstractController {

    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile PersonRepository personRepository;

    @Autowired
    public AdminTeacherController(Map<String, PersonRepository> personRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getAllTeachers(String message) {
        ModelAndView modelAndView = new ModelAndView("/admin_teacher");
        Set<Person> teachers = personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.TEACHER)
                .collect(Collectors.toSet());
        modelAndView.getModel().put("allTeachers", teachers);
        modelAndView.getModel().put("messageFromTeachers", message);
        return modelAndView;
    }

    @GetMapping
    public ModelAndView get() {
        return getAllTeachers("Все учителя");
    }

    @PostMapping("/post")
    public ModelAndView post(@RequestParam("newLastName") String newLastName,
                             @RequestParam("newFirstName") String newFirstName,
                             @RequestParam("newPatronymic") String newPatronymic,
                             @RequestParam("newDateOfBirth") String newDateOfBirth,
                             @RequestParam("newLogin") String newLogin,
                             @RequestParam("newPassword") String newPassword) {
        Person newTeacher = new Teacher()
                .withLastName(newLastName)
                .withFirstName(newFirstName)
                .withPatronymic(newPatronymic)
                .withCredentials(new Credentials()
                        .withLogin(newLogin)
                        .withPassword(newPassword))
                .withDateOfBirth(LocalDate.parse(newDateOfBirth));

        if (!checkTeacherInRepository(personRepository, newTeacher)) {
            log.info("Создание учителя");
            if (personRepository.createPerson(newTeacher)) {
                log.info("Учитель создан");
            } else {
                log.error("Учитель не создан");
            }
        } else {
            log.error("Учитель с введёнными учётными данными уже существует");
            return getAllTeachers("Учитель с введёнными учётными данными уже существует");
        }
        return getAllTeachers("Учитель добавлен");
    }

    @PostMapping("/put/{teacherID}")
    public ModelAndView put(@PathVariable("teacherID") int teacherID,
                            @RequestParam("credentialID") int credentialID,
                            @RequestParam("newLastName") String newLastName,
                            @RequestParam("newFirstName") String newFirstName,
                            @RequestParam("newPatronymic") String newPatronymic,
                            @RequestParam("newLogin") String newLogin,
                            @RequestParam("newPassword") String newPassword,
                            @RequestParam("newDateOfBirth") String newDateOfBirth)
    {
        Person person = personRepository.getPersonById(teacherID);
        if (person == null) {
            log.info("Учитель не найден. Обновления не произошло");
            return getAllTeachers("Учитель не найден. Обновления не произошло");
        } else {
            if (person.getRole() == Role.TEACHER) {
                log.info("Обновление учителя");
                updateTeacher(personRepository, teacherID, credentialID,
                        newLastName, newFirstName, newPatronymic, newDateOfBirth,
                        newLogin, newPassword);
            } else {
                log.error("Пользователь {} {} {} не является учителем", person.getLastName(), person.getFirstName(), person.getPatronymic());
                return getAllTeachers("Пользователь не является учителем");
            }
        }
        return getAllTeachers("Учитель изменён");
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable("id") int id) {
        Person person = personRepository
                .getPersonById(id);
        if (person != null) {
            log.info("Удаление учителя");
            if (personRepository.deletePersonById(id)) {
                log.info("Учитель удален");
            } else {
                log.error("Учитель не удален");
            }
        } else {
            return getAllTeachers("Учитель не найден. Удаления не произошло");
        }
        return getAllTeachers("Учитель удалён");
    }

    private void updateTeacher(PersonRepository personRepository, int teacherID, int credentialID,
                               String newLastName, String newFirstName, String newPatronymic, String newDateOfBirth,
                               String newLogin, String newPassword) {
        Teacher newTeacher = new Teacher()
                .withFirstName(newFirstName)
                .withLastName(newLastName)
                .withPatronymic(newPatronymic)
                .withDateOfBirth(LocalDate.parse(newDateOfBirth))
                .withCredentials(new Credentials()
                        .withLogin(newLogin)
                        .withPassword(newPassword));
        newTeacher.setId(teacherID);
        newTeacher.getCredentials().setId(credentialID);

        if (personRepository.updateAllPersonProperties(newTeacher)) {
            log.info("Учитель обновлён");
        } else {
            log.error("Учитель не обновлён");
        }
    }

    private boolean checkTeacherInRepository(PersonRepository personRepository, Person newTeacher) {
        Person creatableTeacher = personRepository.getPersonByName(newTeacher.getFirstName(), newTeacher.getLastName(), newTeacher.getPatronymic());
        if (creatableTeacher != null) {
            creatableTeacher = personRepository.getPersonByCredentials(newTeacher.getCredentials().getLogin(), newTeacher.getCredentials().getPassword());
            return creatableTeacher != null;
        }
        return false;
    }
}
