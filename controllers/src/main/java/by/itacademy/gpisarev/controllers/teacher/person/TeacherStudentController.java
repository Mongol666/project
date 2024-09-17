package by.itacademy.gpisarev.controllers.teacher.person;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.users.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("teacher/students")
public class TeacherStudentController extends AbstractController {

    private static final String GROUP_REPO_PREFIX = "groupRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;

    private volatile GroupRepository groupRepository;

    @Autowired
    public TeacherStudentController(Map<String, GroupRepository> groupRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public ModelAndView get(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView("/teacher_student");

        Teacher teacher = (Teacher) session.getAttribute("user");
        Group groupWithTeacher = groupRepository.getAllGroups()
                .stream()
                .filter(group -> group.getTeacher().equals(teacher))
                .findAny().orElse(null);

        if (groupWithTeacher == null) {
            modelAndView.getModel().put("allStudents", new HashSet<>());
            modelAndView.getModel().put("messageFromStudents", "Вы не ведёте ни у какой группы...");
            return modelAndView;
        }

        modelAndView.getModel().put("allStudents", groupWithTeacher.getStudents());
        modelAndView.getModel().put("messageFromStudents", "Все студенты");
        return modelAndView;
    }
}
