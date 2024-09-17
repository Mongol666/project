package by.itacademy.gpisarev.controllers.student.group;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.secondary.Group;
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
@RequestMapping("student/groups")
public class StudentGroupController extends AbstractController {
    private static final String GROUP_REPO_PREFIX = "groupRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;

    private volatile GroupRepository groupRepository;

    @Autowired
    public StudentGroupController(Map<String, GroupRepository> groupRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public ModelAndView get() {
        ModelAndView modelAndView = new ModelAndView("/student_groups");
        Set<Group> groups = groupRepository.getAllGroups();
        modelAndView.getModel().put("allGroups", groups);
        modelAndView.getModel().put("messageFromGroups", "Все группы");
        return modelAndView;
    }
}
