package by.itacademy.gpisarev.controllers.admin.person;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.credentials.Credentials;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Student;
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
@RequestMapping("admin/students")
public class AdminStudentController extends AbstractController {

    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile PersonRepository personRepository;

    @Autowired
    public AdminStudentController(Map<String, PersonRepository> personRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getAllStudents(String message) {
        ModelAndView modelAndView = new ModelAndView("/admin_student");
        Set<Person> students = personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.STUDENT)
                .collect(Collectors.toSet());
        modelAndView.getModel().put("allStudents", students);
        modelAndView.getModel().put("messageFromStudents", message);
        return modelAndView;
    }

    @GetMapping
    public ModelAndView get(){
        return getAllStudents("Все студенты");
    }

    @PostMapping
    public ModelAndView post(@RequestParam("newLastName") String newLastName,
                             @RequestParam("newFirstName") String newFirstName,
                             @RequestParam("newPatronymic") String newPatronymic,
                             @RequestParam("newDateOfBirth") String newDateOfBirth,
                             @RequestParam("newLogin") String newLogin,
                             @RequestParam("newPassword") String newPassword) {
        Person newStudent = new Student()
                .withLastName(newLastName)
                .withFirstName(newFirstName)
                .withPatronymic(newPatronymic)
                .withCredentials(new Credentials()
                        .withLogin(newLogin)
                        .withPassword(newPassword))
                .withDateOfBirth(LocalDate.parse(newDateOfBirth));

        if (!checkStudentInRepository(personRepository, newStudent)) {
            log.info("Создание студента");
            if (personRepository.createPerson(newStudent)) {
                log.info("Студент создан");
            } else {
                log.error("Студент не создан");
            }
        } else {
            log.error("Студент с введёнными учётными данными уже существует");
            return getAllStudents("Студент с введёнными учётными данными уже существует");
        }
        return getAllStudents("Студент добавлен");
    }

    @PostMapping("/put/{id}")
    public ModelAndView put(@PathVariable("id") int studentID,
                            @RequestParam("credentialID") int credentialID,
                            @RequestParam("newLastName") String newLastName,
                            @RequestParam("newFirstName") String newFirstName,
                            @RequestParam("newPatronymic") String newPatronymic,
                            @RequestParam("newLogin") String newLogin,
                            @RequestParam("newPassword") String newPassword,
                            @RequestParam("newDateOfBirth") String newDateOfBirth)
    {
        Person person = personRepository.getPersonById(studentID);
        if (person == null) {
            log.info("Студент не найден. Обновления не произошло");
            return getAllStudents("Учитель не найден. Обновления не произошло");
        } else {
            if (person.getRole() == Role.STUDENT) {
                log.info("Обновление студента");
                updateStudent(personRepository, studentID, credentialID,
                        newLastName, newFirstName, newPatronymic, newDateOfBirth,
                        newLogin, newPassword);
            } else {
                log.error("Пользователь {} {} {} не является студентом", person.getLastName(), person.getFirstName(), person.getPatronymic());
                return getAllStudents("Пользователь не является студентом");
            }
        }
        return getAllStudents("Студент изменён");
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable("id") int id){
        Person person = personRepository
                .getPersonById(id);
        if (person != null) {
            log.info("Удаление студента");
            if (personRepository.deletePersonById(id)) {
                log.info("Студент не удалён");
            } else {
                log.error("Студент не удален");
            }
        } else {
            return getAllStudents("Студент не найден. Удаления не произошло");
        }
        return getAllStudents("Студент удалён");
    }

    private void updateStudent(PersonRepository personRepository, int teacherID, int credentialID,
                               String newLastName, String newFirstName, String newPatronymic, String newDateOfBirth,
                               String newLogin, String newPassword) {
        Student newStudent = new Student()
                .withFirstName(newFirstName)
                .withLastName(newLastName)
                .withPatronymic(newPatronymic)
                .withDateOfBirth(LocalDate.parse(newDateOfBirth))
                .withCredentials(new Credentials()
                        .withLogin(newLogin)
                        .withPassword(newPassword));
        newStudent.setId(teacherID);
        newStudent.getCredentials().setId(credentialID);

        if (personRepository.updateAllPersonProperties(newStudent)) {
            log.info("Студент обновлён");
        } else {
            log.error("Студент не обновлён");
        }
    }

    private boolean checkStudentInRepository(PersonRepository personRepository, Person newStudent) {
        log.debug("Проверяется, что пользователя с введённым логином и паролем нет в системе");
        Person creatablePersonOptional = personRepository.getPersonByCredentials(newStudent.getCredentials().getLogin(), newStudent.getCredentials().getPassword());
        return creatablePersonOptional != null;
    }
}
