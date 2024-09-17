package by.itacademy.gpisarev.controllers.admin.mark;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.mark.MarkRepository;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Mark;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Student;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("admin/students/{studentID}/marks")
public class AdminMarksController extends AbstractController {

    private static final String MARK_REPO_PREFIX = "markRepository";
    private static final String PERSON_REPO_PREFIX = "personRepository";
    private static final String SUBJECT_REPO_PREFIX = "subjectRepository";

    private final Map<String, MarkRepository> markRepositoryMap;
    private final Map<String, PersonRepository> personRepositoryMap;
    private final Map<String, SubjectRepository> subjectRepositoryMap;

    private volatile MarkRepository markRepository;
    private volatile PersonRepository personRepository;
    private volatile SubjectRepository subjectRepository;

    public AdminMarksController(Map<String, MarkRepository> markRepositoryMap,
                                Map<String, PersonRepository> personRepositoryMap,
                                Map<String, SubjectRepository> subjectRepositoryMap) {
        this.markRepositoryMap = markRepositoryMap;
        this.personRepositoryMap = personRepositoryMap;
        this.subjectRepositoryMap = subjectRepositoryMap;
    }

    @PostConstruct
    public void init() {
        markRepository = markRepositoryMap.get(MARK_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        subjectRepository = subjectRepositoryMap.get(SUBJECT_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getAllStudentMarks(int studentID, String message) {
        ModelAndView modelAndView = new ModelAndView();
        Student student = getStudentByID(studentID);
        if (student != null) {
            Set<Mark> marks = markRepository.getMarksByStudentID(student.getId());
            student.setMarks(marks);
            modelAndView.setViewName("/admin_students_marks");
            modelAndView.getModel().put("student", student);
            modelAndView.getModel().put("messageFromMarks", message);
        } else {
            log.error("Возврат");
            modelAndView.setViewName("/admin_student");
        }
        return modelAndView;
    }

    private Student getStudentByID(int studentID) {
        Person person = personRepository
                .getPersonById(studentID);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                return (Student) person;
            } else {
                log.error("{} не является студентом.", person);
                return null;
            }
        } else {
            log.error("Студент не найден.");
            return null;
        }
    }

    @GetMapping
    public ModelAndView get(@PathVariable("studentID") int studentID) {
        return getAllStudentMarks(studentID, "Все оценки студента №" + studentID);
    }

    @PostMapping
    public ModelAndView post(@PathVariable("studentID") int studentID,
                             @RequestParam("newMark") int newMark,
                             @RequestParam("newDate") String newDate,
                             @RequestParam("newSubjectName") String newSubjectName) {
        log.info("Поиск студента по ID для присвоения оценки");
        Student student = getStudentByID(studentID);
        if (student != null) {
            log.info("Студент найден");
        } else {
            log.error("Студент не найден");
            return getAllStudentMarks(studentID,"Студент не найден. Создания не произошло");
        }

        log.info("Проверяется, есть ли введённый предмет");
        Subject subject = subjectRepository.getSubjectByName(newSubjectName);
        if (subject != null) {
            log.info("Создание оценки");
            Mark mark = new Mark()
                    .withMark(newMark)
                    .withDateOfMark(LocalDate.parse(newDate))
                    .withSubject(subject);
            if (markRepository.createMark(mark, studentID)) {
                log.info("Оценка добавлена");
            } else {
                log.error("Оценка не добавлена");
            }
        } else {
            log.error("Предмет не найден");
            return getAllStudentMarks(studentID,"Предмет не найден. Создания не произошло");
        }
        return getAllStudentMarks(studentID, "Оценка добавлена");
    }

    @PostMapping("/{markID}/put")
    public ModelAndView put(@PathVariable("markID") int markID, @PathVariable("studentID") int studentID,
                              @RequestParam("newMark") int newMark, @RequestParam("newDate") String newDate,
                              @RequestParam("newSubjectName") String newSubjectName)
    {
        Mark mark = markRepository.getMarkByID(markID);
        if (mark == null) {
            log.error("Оценка не найдена");
            return getAllStudentMarks(studentID,"Оценка не найдена. Обновления не произошло");
        } else {
            log.info("Проверяется, есть ли введённый предмет");
            Subject subject = subjectRepository.getSubjectByName(newSubjectName);
            if (subject != null) {
                log.info("Обновление оценки");
                mark.setMark(newMark);
                mark.setDateOfMark(LocalDate.parse(newDate));
                mark.setSubject(subject);
                if (markRepository.updateMark(mark)) {
                    log.info("Оценка обновлена");
                } else {
                    log.error("Оценка не обновлена");
                }
            } else {
                log.error("Предмет не найден");
                return getAllStudentMarks(studentID, "Предмет не найден. Обновления не произошло");
            }
        }
        return getAllStudentMarks(studentID, "Оценка изменена");
    }

    @PostMapping("/{markID}/delete")
    public ModelAndView delete(@PathVariable("markID") int markID, @PathVariable("studentID") int studentID) {

        Mark mark = markRepository.getMarkByID(markID);
        if (mark != null) {
            log.debug("Удаление оценки");
            if (markRepository.deleteMarkById(markID)) {
                log.info("Оценка удалена");
            } else {
                log.error("Оценка не удалена");
            }
        } else {
            log.error("Оценка не найдена");
            return getAllStudentMarks(studentID,"Оценка не найдена. Удаления не произошло");
        }
        return getAllStudentMarks(studentID, "Оценка удалена");
    }
}