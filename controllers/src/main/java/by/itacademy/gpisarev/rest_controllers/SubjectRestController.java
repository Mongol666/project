package by.itacademy.gpisarev.rest_controllers;

import by.itacademy.gpisarev.entity.AbstractEntity;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.subject.SubjectRepository;
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
@RequestMapping("/rest/subjects")
public class SubjectRestController extends AbstractRestController {
    private static final String SUBJECT_REPO_PREFIX = "subjectRepository";

    private final Map<String, SubjectRepository> subjectRepositoryMap;

    private volatile SubjectRepository subjectRepository;

    @Autowired
    public SubjectRestController(Map<String, SubjectRepository> subjectRepositoryMap) {
        this.subjectRepositoryMap = subjectRepositoryMap;
    }

    @PostConstruct
    public void init() {
        subjectRepository = subjectRepositoryMap.get(SUBJECT_REPO_PREFIX + StringUtils.capitalize(type) + REPO_SUFFIX);
    }

    @GetMapping
    public Set<Subject> getAllSubjects() {
        return subjectRepository.getAllSubjects();
    }

    @GetMapping("/{id}")
    public Subject getSubjectByID(@PathVariable("id") int id) {
        Subject subject = subjectRepository.getSubjectById(id);
        if (subject != null) {
            return subject;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Предмет по ID " + id + " не найден");
        }
    }

    @PostMapping
    public Subject addSubject(@RequestBody Subject newSubject) {
        if (subjectRepository.createSubject(newSubject)) {
            Set<Subject> subjects = subjectRepository.getAllSubjects();
            return Collections.max(subjects, Comparator.comparing(AbstractEntity::getId));
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Предмет не создан");
        }
    }

    @PutMapping("/{id}")
    public Subject updateSubject(@RequestBody Subject newSubject, @PathVariable("id") int id) {
        Subject oldSubject = subjectRepository.getSubjectById(id);
        if (oldSubject != null) {
            newSubject.setId(id);
            if (subjectRepository.updateSubject(newSubject)) {
                return oldSubject;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Предмет не обновлён");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Предмет не обновлён");
        }
    }

    @DeleteMapping("/{id}")
    public Subject deleteSubject(@PathVariable("id") int id) {
        Subject subject = subjectRepository.getSubjectById(id);
        if (subject != null) {
            if (subjectRepository.deleteSubjectById(id)) {
                return subject;
            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Предмет не удален");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Предмет не удалён");
        }
    }
}
