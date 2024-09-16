package by.itacademy.pisarev.salary;

import secondary.Salary;
import users.Teacher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SalaryRepository {
    //Create
    boolean createSalary(Salary salary, int teacher);

    //Read
    Salary getSalaryByID(int salaryID);
    Set<Salary> getSalariesByTeacherId(int teacherID);
    Set<Salary> getSalariesByDateOfSalary(LocalDate dateOfSalary);
    Set<Salary> getAllSalaries();

    //Update
    boolean updateSalary(Salary newSalary);

    //Delete
    boolean deleteSalaryById(int id);
}
