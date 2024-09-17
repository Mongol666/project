package by.itacademy.gpisarev.salary;

import by.itacademy.gpisarev.secondary.Salary;

import java.time.LocalDate;
import java.util.Set;

public interface SalaryRepository {
    //Create
    boolean createSalary(Salary salary, int teacherID);

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
