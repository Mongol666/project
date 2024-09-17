package by.itacademy.gpisarev.controllers.student.group;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("student/groups/{groupID}/subjects")
public class StudentGroupSubjectController extends AbstractController {
    private static final String GROUP_REPO_PREFIX = "groupRepository";
    private static final String SUBJECT_REPO_PREFIX = "subjectRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;
    private final Map<String, SubjectRepository> subjectRepositoryMap;

    private volatile GroupRepository groupRepository;
    private volatile SubjectRepository subjectRepository;

    @Autowired
    public StudentGroupSubjectController(Map<String, GroupRepository> groupRepositoryMap,
                                         Map<String, SubjectRepository> subjectRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
        this.subjectRepositoryMap = subjectRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        subjectRepository = subjectRepositoryMap.get(SUBJECT_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }


    @GetMapping
    public ModelAndView get(@PathVariable("groupID") int groupID) {
        ModelAndView modelAndView = new ModelAndView();
        Group group = groupRepository.getGroupById(groupID);

        if (group != null) {
            Set<Subject> subjects = subjectRepository.getSubjectsByGroupID(groupID);
            group.setSubjects(subjects);

            modelAndView.setViewName("/student_subjects_students_in_group");
            modelAndView.getModel().put("group", group);
            modelAndView.getModel().put("messageFromGroupSubject", "Все предметы в группе");
        } else {
            log.error("Возврат. Группа не найдена");
            modelAndView.setViewName("/student_groups");
        }
        return modelAndView;
    }
}
