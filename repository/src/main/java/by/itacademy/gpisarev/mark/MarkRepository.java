package by.itacademy.gpisarev.mark;

import by.itacademy.gpisarev.secondary.Mark;

import java.util.Set;

public interface MarkRepository {
    //Create
    boolean createMark(Mark mark, int studentID);

    //Read
    Mark getMarkByID(int id);
    Set<Mark> getAllMarks();
    Set<Mark> getMarksByStudentID(int studentID);

    //Update
    boolean updateMark(Mark newMark);

    //Delete
    boolean deleteMarkById(int id);
}
