package by.itacademy.gpisarev.group;

import by.itacademy.gpisarev.secondary.Group;
import by.itacademy.gpisarev.secondary.Subject;
import by.itacademy.gpisarev.users.Student;

import java.util.Set;

public interface GroupRepository {
    //Create
    boolean createGroup(Group group);

    //Read
    Group getGroupById(int id);
    Group getGroupByName(String name);
    Set<Group> getAllGroups();

    //Update
    boolean updateGroup(Group newGroup);
    boolean updateStudentsAdd(Group group, Student newStudent);
    boolean updateStudentsRemove(Group group, Student removableStudent);
    boolean updateSubjectsAdd(Group group, Subject newSubject);
    boolean updateSubjectsRemove(Group group, Subject removableSubject);

    //Delete
    boolean deleteGroupById(int id);
}
