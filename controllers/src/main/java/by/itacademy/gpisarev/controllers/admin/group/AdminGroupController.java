package by.itacademy.gpisarev.controllers.admin.group;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Group;
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
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("admin/groups")
public class AdminGroupController extends AbstractController {
    private static final String GROUP_REPO_PREFIX = "groupRepository";
    private static final String PERSON_REPO_PREFIX = "personRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;
    private final Map<String, PersonRepository> personRepositoryMap;

    private volatile GroupRepository groupRepository;
    private volatile PersonRepository personRepository;

    @Autowired
    public AdminGroupController(Map<String, GroupRepository> groupRepositoryMap,
                                Map<String, PersonRepository> personRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
        this.personRepositoryMap = personRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    private ModelAndView getAllGroups(String message) {
        ModelAndView modelAndView = new ModelAndView("/admin_groups");
        Set<Group> groups = groupRepository.getAllGroups();
        modelAndView.getModel().put("allGroups", groups);
        modelAndView.getModel().put("messageFromGroups", message);
        return modelAndView;
    }


    @GetMapping
    public ModelAndView get(){
        return getAllGroups("Все группы");
    }

    @PostMapping
    public ModelAndView post(@RequestParam("lastFirstPatronymic") String lastFirstPatronymic,
                               @RequestParam("newName") String newName)
    {
        Group group;

        String lastNameOfTeacher = lastFirstPatronymic.split(" ")[0];
        String firstNameOfTeacher = lastFirstPatronymic.split(" ")[1];
        String patronymicOfTeacher = lastFirstPatronymic.split(" ")[2];

        log.debug("Проверка, есть ли такой учитель");
        Person teacher = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);

        if (teacher != null) {
            if (teacher.getRole() == Role.TEACHER) {
                group = new Group()
                        .withName(newName)
                        .withTeacher((Teacher) teacher);
            } else {
                group = new Group()
                        .withName(newName);
            }
        } else {
            group = new Group()
                    .withName(newName);
        }

        log.info("Создание группы");
        if (groupRepository.createGroup(group)) {
            log.info("Группа создана");
            return getAllGroups("Группа добавлена");
        } else {
            return getAllGroups("Группа не добавлена");
        }
    }

    @PostMapping("/put/{id}")
    public ModelAndView put(@PathVariable("id") int id,
                         @RequestParam("newLastFirstPatronymic") String newLastFirstPatronymic,
                         @RequestParam("newName") String newName) {
        Group group = groupRepository.getGroupById(id);
        if (group == null) {
            log.info("Группа не найдена. Обновления не произошло");
            return getAllGroups("Группа не найдена. Обновления не произошло");
        } else {
            String lastNameOfTeacher = newLastFirstPatronymic.split(" ")[0];
            String firstNameOfTeacher = newLastFirstPatronymic.split(" ")[1];
            String patronymicOfTeacher = newLastFirstPatronymic.split(" ")[2];

            log.debug("Проверка, есть ли учитель, который будет вести эту группу");
            Person newTeacher = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);
            if (newTeacher != null && newTeacher.getRole() == Role.TEACHER) {
                group.setName(newName);
                group.setTeacher((Teacher) newTeacher);
                if (groupRepository.updateGroup(group)) {
                    log.info("Группа обновлена");
                } else {
                    log.error("Группа не обновлена");
                }
            } else {
                log.error("Учитель не найден или пользователь - не учитель");
                return getAllGroups("Учитель не найден или пользователь - не учитель");
            }
        }
        return getAllGroups("Группа изменена");
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable("id") int id) {
        Group group = groupRepository.getGroupById(id);
        if (group != null) {
            log.debug("Удаление группы");
            if (groupRepository.deleteGroupById(id)) {
                log.info("Группа удалена");
            } else {
                log.error("Группа не удалена");
            }
        } else {
            return getAllGroups("Группа не найдена. Удаления не произошло");
        }
        return getAllGroups("Группа удалена");
    }
}
