package by.itacademy.gpisarev.controllers.student.group;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.users.Student;
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
@RequestMapping("student/groups/{groupID}/students")
public class StudentGroupStudentController extends AbstractController {
    private static final String GROUP_REPO_PREFIX = "groupRepository";
    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;
    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile GroupRepository groupRepository;
    private volatile PersonRepository personRepository;

    @Autowired
    public StudentGroupStudentController(Map<String, GroupRepository> groupRepositoryMap,
                                         Map<String, PersonRepository> personRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public ModelAndView get(@PathVariable("groupID") int groupID) {
        ModelAndView modelAndView = new ModelAndView();
        Group group = groupRepository.getGroupById(groupID);

        if (group != null) {
            Set<Student> students = personRepository.getStudentsByGroupID(groupID);
            group.setStudents(students);

            modelAndView.setViewName("/student_subjects_students_in_group");
            modelAndView.getModel().put("group", group);
            modelAndView.getModel().put("messageFromGroupStudent", "Все студенты в группе");
        } else {
            log.error("Возврат");
            modelAndView.setViewName("/student_groups");
        }
        return modelAndView;
    }
}
