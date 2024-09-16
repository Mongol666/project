package by.itacademy.pisarev.mark;

import secondary.Mark;
import secondary.Subject;
import users.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
