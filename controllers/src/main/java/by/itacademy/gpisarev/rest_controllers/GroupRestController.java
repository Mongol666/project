package by.itacademy.gpisarev.rest_controllers;

import by.itacademy.gpisarev.entity.AbstractEntity;
import by.itacademy.gpisarev.group.GroupRepository;
import by.itacademy.gpisarev.person.PersonRepository;
import by.itacademy.gpisarev.role.Role;
import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
import by.itacademy.gpisarev.users.Person;
import by.itacademy.gpisarev.users.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/rest/groups")
public class GroupRestController extends AbstractRestController {
    private static final String GROUP_REPO_PREFIX = "groupRepository";
    private static final String PERSON_REPO_PREFIX = "personRepository";
    private static final String SUBJECT_REPO_PREFIX = "subjectRepository";

    private final Map<String, GroupRepository> groupRepositoryMap;
    private final Map<String, PersonRepository> personRepositoryMap;
    private final Map<String, SubjectRepository> subjectRepositoryMap;

    private volatile GroupRepository groupRepository;
    private volatile PersonRepository personRepository;
    private volatile SubjectRepository subjectRepository;

    @Autowired
    public GroupRestController(Map<String, GroupRepository> groupRepositoryMap,
                               Map<String, PersonRepository> personRepositoryMap,
                               Map<String, SubjectRepository> subjectRepositoryMap) {
        this.groupRepositoryMap = groupRepositoryMap;
        this.personRepositoryMap = personRepositoryMap;
        this.subjectRepositoryMap = subjectRepositoryMap;
    }

    @PostConstruct
    public void init() {
        groupRepository = groupRepositoryMap.get(GROUP_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        personRepository = personRepositoryMap.get(PERSON_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
        subjectRepository = subjectRepositoryMap.get(SUBJECT_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public Set<Group> getAllGroups() {
        return groupRepository.getAllGroups();
    }

    @GetMapping("/{groupID}")
    public Group getGroupByID(@PathVariable("groupID") int groupID) {
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            return group;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Группа по ID " + groupID + " не найдена");
        }
    }

    @PostMapping
    public Group addGroup(@RequestBody Group newGroup) {
        if (groupRepository.createGroup(newGroup)) {
            Set<Group> groups = groupRepository.getAllGroups();
            return Collections.max(groups, Comparator.comparing(AbstractEntity::getId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Группа не создана");
        }
    }

    @PostMapping("/{groupID}/students/{studentID}")
    public Group addStudentToGroup(@PathVariable("groupID") int groupID, @PathVariable("studentID") int studentID) {
        Group group = groupRepository.getGroupById(groupID);
        Person person = personRepository.getPersonById(studentID);

        if (group != null && person != null && person.getRole() == Role.STUDENT) {
            if (groupRepository.updateStudentsAdd(group, (Student) person)) {
                return groupRepository.getGroupById(groupID);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Студент №" + studentID + " не добавлен в группу №" + groupID);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Студент или группа не соответствуют требованиям");
        }
    }

    @PostMapping("/{groupID}/subjects/{subjectID}")
    public Group addSubjectToGroup(@PathVariable("groupID") int groupID, @PathVariable("subjectID") int subjectID) {
        Group group = groupRepository.getGroupById(groupID);
        Subject subject = subjectRepository.getSubjectById(subjectID);

        if (group != null && subject != null) {
            if (groupRepository.updateSubjectsAdd(group, subject)) {
                return groupRepository.getGroupById(groupID);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Предмет №" + subjectID + " не добавлен в группу №" + groupID);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Предмет или группа не соответствуют требованиям");
        }
    }

    @PutMapping("/{groupID}")
    public Group updateGroup(@RequestBody Group newGroup, @PathVariable("groupID") int groupID) {
        Group oldGroup = groupRepository.getGroupById(groupID);
        if (oldGroup != null) {
            newGroup.setId(groupID);
            if (groupRepository.updateGroup(newGroup)) {
                return oldGroup;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Группа не обновлена");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Группа не обновлена");
        }
    }

    @DeleteMapping("/{groupID}")
    public Group deleteGroup(@PathVariable("groupID") int groupID) {
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            if (groupRepository.deleteGroupById(groupID)) {
                return group;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Группа не удалена");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Группа не удалена");
        }
    }

    @DeleteMapping("/{groupID}/students/{studentID}")
    public Group deleteStudentFromGroup(@PathVariable("groupID") int groupID, @PathVariable("studentID") int studentID) {
        Group group = groupRepository.getGroupById(groupID);
        Person person = personRepository.getPersonById(studentID);
        if (group != null && person != null && person.getRole() == Role.STUDENT) {
            if (groupRepository.updateStudentsRemove(group, (Student) person)) {
                return groupRepository.getGroupById(groupID);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Студент №" + studentID + " не удалён из группы №" + groupID);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Студент или группа не соответствуют требованиям");
        }
    }

    @DeleteMapping("/{groupID}/subjects/{subjectID}")
    public Group deleteSubjectFromGroup(@PathVariable("groupID") int groupID, @PathVariable("subjectID") int subjectID) {
        Group group = groupRepository.getGroupById(groupID);
        Subject subject = subjectRepository.getSubjectById(subjectID);
        if (group != null && subject != null) {
            if (groupRepository.updateSubjectsRemove(group, subject)) {
                return groupRepository.getGroupById(groupID);
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Предмет №" + subjectID + " не удалён из группы №" + groupID);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Предмет или группа не соответствуют требованиям");
        }
    }
}
