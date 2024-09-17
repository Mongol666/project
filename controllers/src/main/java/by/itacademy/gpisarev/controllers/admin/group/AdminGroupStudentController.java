package by.itacademy.gpisarev.controllers.admin.group;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Group;
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
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("admin/groups/{groupID}/students")
public class AdminGroupStudentController extends AbstractController {

    private static final String GROUP_REPO_PREFIX = "groupRepository";
    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;
    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile GroupRepository groupRepository;
    private volatile PersonRepository personRepository;

    @Autowired
    public AdminGroupStudentController(Map<String, GroupRepository> groupRepositoryMap,
                                       Map<String, PersonRepository> personRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getGroup(int groupID, String message) {
        ModelAndView modelAndView = new ModelAndView();
        Group group = getGroupByID(groupID);

        if (group != null) {
            modelAndView.setViewName("/admin_subjects_students_in_group");
            modelAndView.getModel().put("group", group);
            modelAndView.getModel().put("messageFromGroupStudent", message);
        } else {
            log.error("Возврат");
            modelAndView.setViewName("/admin_groups");
        }
        return modelAndView;
    }

    private Group getGroupByID(int groupID) {
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            Set<Student> students = personRepository.getStudentsByGroupID(groupID);
            group.setStudents(students);
            return group;
        } else {
            log.error("Группа не найдена.");
            return null;
        }
    }

    private Student getStudentByID(int studentID) {
        Person person = personRepository.getPersonById(studentID);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                return (Student) person;
            } else {
                log.error("{} не является студентом", person);
                return null;
            }
        } else {
            log.error("Студент не найден");
            return null;
        }
    }

    private Student getStudentByNames(String lastName, String firstName, String patronymic) {
        Person person = personRepository.getPersonByName(firstName, lastName, patronymic);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                return (Student) person;
            } else {
                log.error("{} не является студентом", person);
                return null;
            }
        } else {
            log.error("Студент не найден");
            return null;
        }
    }

    @GetMapping
    public ModelAndView get(@PathVariable("groupID") int groupID) {
        return getGroup(groupID, "Все студенты в группе");
    }

    @PostMapping
    public ModelAndView post(@PathVariable("groupID") int groupID,
                             @RequestParam("newLastName") String newLastName,
                             @RequestParam("newFirstName") String newFirstName,
                             @RequestParam("newPatronymic") String newPatronymic)
    {
        Group group = getGroupByID(groupID);
        Student student = getStudentByNames(newLastName, newFirstName, newPatronymic);
        if (group != null) {
            if (student != null) {
                if (groupRepository.updateStudentsAdd(group, student)) {
                    log.info("Студент добавлен в группу");
                } else {
                    log.error("Студент не добавлен в группу");
                }
            } else {
                log.error("Студент не найден");
                return getGroup(groupID, "Студент не найден. Добавления не произошло");
            }
        } else {
            log.error("Группа не найдена");
            return getGroup(groupID, "Группа не найдена. Добавления не произошло");
        }
        return getGroup(groupID, "Студент добавлен");
    }

    @PostMapping("/{studentID}/delete")
    public ModelAndView delete(@PathVariable("studentID") int studentID, @PathVariable("groupID") int groupID) {
        Group group = getGroupByID(groupID);
        Student student = getStudentByID(studentID);

        if (group != null) {
            if (student != null) {
                if (groupRepository.updateStudentsRemove(group, student)) {
                    log.info("Студент удален из группы");
                } else {
                    log.error("Студент не удален из группы");
                }
            } else {
                log.error("Студент не найден");
                return getGroup(groupID, "Студент не найден. Удаления не произошло");
            }
        } else {
            log.error("Группа не найдена");
            return getGroup(groupID, "Группа не найден. Удаления не произошло");
        }
        return getGroup(groupID, "Студент удалён");
    }
}
