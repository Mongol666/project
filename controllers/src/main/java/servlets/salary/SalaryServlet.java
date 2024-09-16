package servlets.salary;

import by.itacademy.pisarev.person.PersonRepository;
import by.itacademy.pisarev.salary.SalaryRepository;
import lombok.extern.slf4j.Slf4j;
import role.Role;
import secondary.Salary;
import servlets.AbstractServlet;
import users.Person;
import users.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

@Slf4j
@WebServlet("/SalaryServlet")
public class SalaryServlet extends AbstractServlet {

    private void getAllTeacherSalaries(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");

        Teacher teacher = getTeacherByID(request);
        if (teacher != null) {
            Set<Salary> salaries = salaryRepository.getSalariesByTeacherId(teacher.getId());
            teacher.setSalaries(salaries);
            request.setAttribute("teacher", teacher);
            forward("/admin_salary.jsp", request, response);
        } else {
            log.error("Возврат");
            forward("/admin_teacher.jsp", request, response);
        }
    }

    private Teacher getTeacherByID(HttpServletRequest request) {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        Person teacher = personRepository
                .getPersonById(Integer.parseInt(request.getParameter("teacherID")));
        if (teacher != null) {
            if (teacher.getRole() == Role.TEACHER) {
                return (Teacher) teacher;
            } else {
                log.error("{} не является учителем.", teacher);
                return null;
            }
        } else {
            log.error("Учитель не найден.");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllTeacherSalaries(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");

        log.debug("Получение данных о новой зарплате");
        LocalDate newDateOfSalary = LocalDate.parse(req.getParameter("newDateOfSalary"));
        int newSalary = Integer.parseInt(req.getParameter("newCount"));

        log.debug("Получение ID учителя, которому назначается зарплата");
        int teacherID = Integer.parseInt(req.getParameter("teacherID"));

        log.debug("Поиск учителя по ID для присвоения зарплаты");
        Teacher teacher = getTeacherByID(req);
        if (teacher != null) {
            log.info("Учитель найден");
        } else {
            getAllTeacherSalaries(req, resp);
        }

        Salary salary = new Salary()
                .withSalary(newSalary)
                .withDateOfSalary(newDateOfSalary);
        log.info("Создание зарплаты");
        if (salaryRepository.createSalary(salary, teacherID)) {
            log.info("Зарплата создана");
        } else {
            log.error("Зарплата не создана");
        }
        getAllTeacherSalaries(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");

        log.debug("Получение ID зарплаты для изменения");
        int id = Integer.parseInt(req.getParameter("ID"));

        Salary salary = salaryRepository.getSalaryByID(id);
        if (salary == null) {
            log.error("Зарплата не найдена");
            req.setAttribute("salaryNotFound", "Зарплата не найдена");
            getAllTeacherSalaries(req, resp);
        } else {
            log.debug("Получение новых данных о зарплате");
            int newSalary = Integer.parseInt(req.getParameter("newSalary"));
            LocalDate newDateOfSalary = LocalDate.parse(req.getParameter("newDateOfSalary"));

            salary.setSalary(newSalary);
            salary.setDateOfSalary(newDateOfSalary);

            log.debug("Обновление зарплаты");
            if (salaryRepository.updateSalary(salary)) {
                log.info("Зарплата обновлена");
            } else {
                log.error("Зарплата не обновлена");
            }
        }
        getAllTeacherSalaries(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SalaryRepository salaryRepository = (SalaryRepository) getServletContext().getAttribute("salary_repository");

        log.debug("Получение ID зарплаты для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Удаление зарплаты");
        if (salaryRepository.deleteSalaryById(id)) {
            log.info("Зарплата удалена");
        } else {
            log.error("Зарплата не удалена");
        }

        getAllTeacherSalaries(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getParameter("method")) {
            case "delete":
                doDelete(req, resp);
                break;
            case "put":
                doPut(req, resp);
                break;
            case "post":
                doPost(req, resp);
                break;
            case "get":
                doGet(req, resp);
                break;
        }
    }
}
