package by.itacademy.gpisarev.controllers.admin.salary;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.salary.SalaryRepository;
import by.itacademy.gpisarev.secondary.Salary;
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

@Slf4j
@Controller
@RequestMapping("admin/teachers/{teacherID}/salaries")
public class AdminSalaryController extends AbstractController {

    private static final String PERSON_REPO_PREFIX = "personRepository";
    private static final String SALARY_REPO_PREFIX = "salaryRepository";

    private final Map<String, PersonRepository> personRepositoryMap;
    private final Map<String, SalaryRepository> salaryRepositoryMap;

    private volatile PersonRepository personRepository;
    private volatile SalaryRepository salaryRepository;

    @Autowired
    public AdminSalaryController(Map<String, PersonRepository> personRepositoryMap,
                                 Map<String, SalaryRepository> salaryRepositoryMap) {
        this.personRepositoryMap = personRepositoryMap;
        this.salaryRepositoryMap = salaryRepositoryMap;
    }

    @PostConstruct
    public void init() {
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        salaryRepository = salaryRepositoryMap.get(SALARY_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getAllTeacherSalaries(int teacherID, String message) {
        ModelAndView modelAndView = new ModelAndView();
        Teacher teacher = getTeacherByID(teacherID);
        if (teacher != null) {
            Set<Salary> salaries = salaryRepository.getSalariesByTeacherId(teacher.getId());
            teacher.setSalaries(salaries);
            modelAndView.setViewName("/admin_teachers_salaries");
            modelAndView.getModel().put("teacher", teacher);
            modelAndView.getModel().put("messageFromSalary", message);
        } else {
            modelAndView.setViewName("/admin_teacher");
            log.error("Возврат");
        }
        return modelAndView;
    }

    private Teacher getTeacherByID(int teacherID) {
        Person teacher = personRepository
                .getPersonById(teacherID);
        if (teacher != null) {
            if (teacher.getRole() == Role.TEACHER) {
                return (Teacher) teacher;
            } else {
                log.error("{} не является учителем.", teacher);
                return null;
            }
        } else {
            log.error("Учитель не найден.");
            return null;
        }
    }

    @GetMapping
    public ModelAndView get(@PathVariable("teacherID") int teacherID){
        return getAllTeacherSalaries(teacherID, "Все зарплаты учителя №" + teacherID);
    }

    @PostMapping
    public ModelAndView post(@RequestParam("newDateOfSalary") String newDateOfSalary, @RequestParam("newSalary") int newSalary,
                             @PathVariable("teacherID") int teacherID) {
        String message;

        log.debug("Поиск учителя по ID для присвоения зарплаты");
        Teacher teacher = getTeacherByID(teacherID);
        if (teacher != null) {
            log.info("Учитель найден");
        } else {
            log.error("Учитель не найден");
            message = "Учитель не найден. Создания не произошло";
            return getAllTeacherSalaries(teacherID, message);
        }

        Salary salary = new Salary()
                .withSalary(newSalary)
                .withDateOfSalary(LocalDate.parse(newDateOfSalary));
        log.info("Создание зарплаты");
        if (salaryRepository.createSalary(salary, teacherID)) {
            log.info("Зарплата создана");
            return getAllTeacherSalaries(teacherID, "Зарплата добавлена");
        } else {
            log.error("Зарплата не создана");
            return getAllTeacherSalaries(teacherID, "Зарплата не добавлена");
        }
    }

    @PostMapping("/{salaryID}/put")
    public ModelAndView put(@PathVariable("teacherID") int teacherID, @PathVariable("salaryID") int salaryID,
                            @RequestParam("newSalary") int newSalary, @RequestParam("newDateOfSalary") String newDateOfSalary) {

        String message;

        Salary salary = salaryRepository.getSalaryByID(salaryID);
        if (salary == null) {
            log.error("Зарплата не найдена");
            message = "Зарплата не найдена. Обновления не произошло";
            return getAllTeacherSalaries(teacherID, message);
        } else {
            log.debug("Обновление зарплаты");
            salary.setSalary(newSalary);
            salary.setDateOfSalary(LocalDate.parse(newDateOfSalary));

            if (salaryRepository.updateSalary(salary)) {
                log.info("Зарплата обновлена");
                return getAllTeacherSalaries(teacherID, "Зарплата обновлена");
            } else {
                log.error("Зарплата не обновлена");
                return getAllTeacherSalaries(teacherID, "Зарплата не обновлена");
            }
        }
    }

    @PostMapping("/{salaryID}/delete")
    public ModelAndView delete(@PathVariable("teacherID") int teacherID, @PathVariable("salaryID") int salaryID)  {
        String message;

        Salary salary = salaryRepository.getSalaryByID(salaryID);
        if (salary != null) {
            log.debug("Удаление зарплаты");
            if (salaryRepository.deleteSalaryById(salaryID)) {
                log.info("Зарплата удалена");
            } else {
                log.error("Зарплата не удалена");
            }
        } else {
            message = "Зарплата не найдена. Удаления не произошло";
            return getAllTeacherSalaries(teacherID, message);
        }
        return getAllTeacherSalaries(teacherID, "Зарплата удалена");
    }

    @PostMapping("/patch")
    public ModelAndView patch(@PathVariable("teacherID") int teacherID) {
        Person person = personRepository.getPersonById(teacherID);
        if (person != null) {
            if (person.getRole() == Role.TEACHER) {
                Teacher teacher = (Teacher) person;
                double averageSalary = teacher.getSalaries().stream().mapToInt(Salary::getSalary).average().orElse(0.0);
                return getAllTeacherSalaries(teacherID, String.valueOf(averageSalary));
            } else {
                return getAllTeacherSalaries(teacherID, "Пользователь не является учителем");
            }
        } else {
            return getAllTeacherSalaries(teacherID, "Учитель не найден");
        }
    }

}
