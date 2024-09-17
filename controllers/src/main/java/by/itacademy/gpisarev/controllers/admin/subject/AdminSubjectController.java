package by.itacademy.gpisarev.controllers.admin.subject;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
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
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("admin/subjects")
public class AdminSubjectController extends AbstractController {

    private static final String SUBJECT_REPO_PREFIX = "subjectRepository";

    private final Map<String, SubjectRepository> subjectRepositoryMap;

    private volatile SubjectRepository subjectRepository;

    @Autowired
    public AdminSubjectController(Map<String, SubjectRepository> subjectRepositoryMap) {
        this.subjectRepositoryMap = subjectRepositoryMap;
    }

    @PostConstruct
    public void init() {
        subjectRepository = subjectRepositoryMap.get(SUBJECT_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getAllSubjects(String message) {
        ModelAndView modelAndView = new ModelAndView("/admin_subjects");
        Set<Subject> subjects = subjectRepository.getAllSubjects();
        modelAndView.getModel().put("allSubjects", subjects);
        modelAndView.getModel().put("messageFromSubject", message);
        return modelAndView;
    }

    @GetMapping
    public ModelAndView get() {
        return getAllSubjects("Все предметы");
    }

    @PostMapping
    public ModelAndView post(@RequestParam("newName") String newName) {
        log.debug("Создание предмета");
        Subject subject = new Subject()
                .withName(newName);
        if (subjectRepository.createSubject(subject)) {
            log.info("Предмет создан");
        } else {
            log.error("Предмет не создан");
        }
        return getAllSubjects("Предмет добавлен");
    }

    @PostMapping("/put/{id}")
    public ModelAndView put(@PathVariable("id") int id, @RequestParam("newName") String newName) {
        log.info("Попытка получения предмета по ID");
        Subject subject = subjectRepository.getSubjectById(id);
        if (subject != null) {
            log.debug("Обновление предмета");
            subject.setName(newName);
            if (subjectRepository.updateSubject(subject)) {
                log.info("Предмет обновлён");
            } else {
                log.error("Предмет не обновлен");
            }
        } else {
            return getAllSubjects("Предмет не найден. Обновления не произошло");
        }
        return getAllSubjects("Предмет изменён");
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable("id") int id){
        log.info("Попытка получения предмета по ID");
        Subject subject = subjectRepository.getSubjectById(id);
        if (subject != null) {
            log.debug("Удаление предмета");
            if (subjectRepository.deleteSubjectById(id)) {
                log.info("Предмет удалён");
            } else {
                log.error("Предмет не удалён");
            }
        } else {
            return getAllSubjects("Предмет не найден. Удаления не произошло");
        }
        return getAllSubjects("Предмет удалён");
    }
}
