package servlets.system;

import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import by.itacademy.pisarev.person.PersonRepository;
import users.Student;
import users.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@Slf4j
@WebServlet(name = "RegistrationServlet", value = "/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Получение данных о новом пользователе");

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String patronymic = req.getParameter("patronymic");

        LocalDate dateOfBirth = LocalDate.parse(req.getParameter("dateOfBirth"));

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        String roleFromRequest = req.getParameter("role");

        PersonRepository repository = (PersonRepository) getServletContext().getAttribute("person_repository");

        switch (roleFromRequest) {
            case "Студент":
                repository.createPerson(new Student()
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withPatronymic(patronymic)
                        .withDateOfBirth(dateOfBirth)
                        .withCredentials(new Credentials()
                                .withLogin(login)
                                .withPassword(password)));
                log.info("Студент успешно добавлен");
                break;
            case "Учитель":
                repository.createPerson(new Teacher()
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withPatronymic(patronymic)
                        .withDateOfBirth(dateOfBirth)
                        .withCredentials(new Credentials()
                                .withLogin(login)
                                .withPassword(password)));
                log.info("Учитель успешно добавлен");
                break;
        }
        req.setAttribute("create", "Регистрация прошла успешно!");
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
