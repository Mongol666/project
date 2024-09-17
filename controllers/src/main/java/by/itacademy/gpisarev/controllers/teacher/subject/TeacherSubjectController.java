package by.itacademy.gpisarev.controllers.teacher.subject;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
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

@Slf4j
@Controller
@RequestMapping("teacher/subjects")
public class TeacherSubjectController extends AbstractController {

    private static final String SUBJECT_REPO_PREFIX = "subjectRepository";

    private final Map<String, SubjectRepository> subjectRepositoryMap;

    private volatile SubjectRepository subjectRepository;

    @Autowired
    public TeacherSubjectController(Map<String, SubjectRepository> subjectRepositoryMap) {
        this.subjectRepositoryMap = subjectRepositoryMap;
    }

    @PostConstruct
    public void init() {
        subjectRepository = subjectRepositoryMap.get(SUBJECT_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public ModelAndView getAllSubjects() {
        ModelAndView modelAndView = new ModelAndView("/teacher_subjects");
        Set<Subject> subjects = subjectRepository.getAllSubjects();
        modelAndView.getModel().put("allSubjects", subjects);
        modelAndView.getModel().put("messageFromSubject", "Все предметы");
        return modelAndView;
    }
}
