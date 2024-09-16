package by.itacademy.pisarev.subject;

import secondary.Subject;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubjectRepository {
    //Create
    boolean createSubject(Subject subject);

    //Read
    Subject getSubjectById(int id);
    Subject getSubjectByName(String name);
    Set<Subject> getSubjectsByGroupID(int groupID);
    Set<Subject> getAllSubjects();

    //Update
    boolean updateSubject(Subject newSubject);

    //Delete
    boolean deleteSubjectById(int id);
}
