package servlets.system;

import by.itacademy.pisarev.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import users.Person;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebServlet(name = "LogInServlet", value = "/LogInServlet")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        log.info("Получены логин и пароль пользователя, идёт проверка...");

        RequestDispatcher dispatcher;
        Person person = checkRightLoginAndPassword(login, password);
        if (person != null) {
            switch (person.getClass().getName()) {
                case "users.Admin":
                    request.getSession().setAttribute("user", person);
                    dispatcher = request.getRequestDispatcher("/admin.jsp");
                    dispatcher.forward(request, response);
                    break;
                case "users.Teacher":
                    request.getSession().setAttribute("user", person);
                    dispatcher = request.getRequestDispatcher("/teacher.jsp");
                    dispatcher.forward(request, response);
                    break;
                case "users.Student":
                    request.getSession().setAttribute("user", person);
                    dispatcher = request.getRequestDispatcher("/student.jsp");
                    dispatcher.forward(request, response);
                    break;
                default:
                    request.setAttribute("error", "Не существует такого типа пользователя...");
                    dispatcher = request.getRequestDispatcher("/index.jsp");
                    dispatcher.forward(request, response);
            }
        } else {
            request.setAttribute("error", "Неправильный логин или пароль...");
            dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
        }
    }

    private Person checkRightLoginAndPassword(String login, String password) {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Person person = personRepository.getPersonByCredentials(login, password);
        if (person != null) {
            return person;
        }
        log.error("Введены неправильные логин или пароль...");
        return null;
    }

}
