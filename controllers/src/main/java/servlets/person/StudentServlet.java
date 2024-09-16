package servlets.person;

import by.itacademy.pisarev.person.PersonRepository;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import role.Role;
import servlets.AbstractServlet;
import users.Person;
import users.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet("/StudentServlet")
@Slf4j
public class StudentServlet extends AbstractServlet {

    private Set<Person> getAllStudents() {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        return personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.STUDENT)
                .collect(Collectors.toSet());

    }

    private void getAllStudentsForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Set<Person> students = getAllStudents();
        request.setAttribute("students", students);
        forward("/admin_student.jsp", request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllStudentsForward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получение данных о пользователе (студенте) и его создание");
        Person newStudent = new Student()
                .withLastName(req.getParameter("newLastName"))
                .withFirstName(req.getParameter("newFirstName"))
                .withPatronymic(req.getParameter("newPatronymic"))
                .withCredentials(new Credentials()
                        .withLogin(req.getParameter("newLogin"))
                        .withPassword(req.getParameter("newPassword")))
                .withDateOfBirth(LocalDate.parse(req.getParameter("newDateOfBirth")));

        if (checkStudentInRepository(personRepository, newStudent)) {
            log.info("Создание студента");
            if (personRepository.createPerson(newStudent)) {
                log.info("Студент создан");
            } else {
                log.error("Студент не создан");
            }
        } else {
            String messageAboutNotCreating = "Студент с введёнными учётными данными уже существует";
            req.setAttribute("message", messageAboutNotCreating);
            getAllStudentsForward(req, resp);
        }

        getAllStudentsForward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        log.info("Получения ID пользователя (студента) для обновления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.info("Получение ID учётных данных (студент) для обновления");
        int credentialID = Integer.parseInt(req.getParameter("credentialID"));

        Person updatableStudent = personRepository.getPersonById(id);
        if (updatableStudent == null) {
            String messageAboutNotUpdating = "Студент не обновлён";
            req.setAttribute("message", messageAboutNotUpdating);
            getAllStudentsForward(req, resp);
        } else {
            if (updatableStudent.getRole() == Role.STUDENT) {
                log.info("Обновление студента");
                updateStudent(req, personRepository, id, credentialID);
            } else {
                String messageAboutNotUpdating = "Студент не обновлён";
                req.setAttribute("message", messageAboutNotUpdating);
                getAllStudentsForward(req, resp);
            }
        }

        getAllStudentsForward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получения ID пользователя (студента) для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        Person removableStudent = personRepository
                .getPersonById(id);
        if (removableStudent != null) {
            log.info("Удаление студента");
            if (personRepository.deletePersonById(id)) {
                log.info("Студент не удалён");
            } else {
                log.error("Студент не удален");
            }
        } else {
            String messageAboutNotDeleting = "Студент не удалён";
            req.setAttribute("message", messageAboutNotDeleting);
        }

        getAllStudentsForward(req, resp);
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

    private void updateStudent(HttpServletRequest req, PersonRepository personRepository, int id, int credentialID) {
        log.info("Получение новых фамилии, имени и отчества пользователя (студента) для обновления");
        String newFirstName = req.getParameter("newFirstName");
        String newLastName = req.getParameter("newLastName");
        String newPatronymic = req.getParameter("newPatronymic");

        log.info("Получение новых логина и пароля пользователя (студента) для обновления");
        String newLogin = req.getParameter("newLogin");
        String newPassword = req.getParameter("newPassword");

        log.info("Получение новой даты рождения пользователя (студента) для обновления");
        String newDateOfBirthString = req.getParameter("newDateOfBirth");
        LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);

        Student newStudent = new Student()
                .withFirstName(newFirstName)
                .withLastName(newLastName)
                .withPatronymic(newPatronymic)
                .withDateOfBirth(newDateOfBirth)
                .withCredentials(new Credentials()
                        .withLogin(newLogin)
                        .withPassword(newPassword));
        newStudent.setId(id);
        newStudent.getCredentials().setId(credentialID);

        if (personRepository.updateAllPersonProperties(newStudent)) {
            log.info("Студент обновлён");
        } else {
            log.error("Студент не обновлён");
        }

    }

    private boolean checkStudentInRepository(PersonRepository personRepository, Person newStudent) {
        log.debug("Проверяется, что пользователя с введённым логином и паролем нет в системе");
        Person creatablePersonOptional = personRepository.getPersonByCredentials(newStudent.getCredentials().getLogin(), newStudent.getCredentials().getPassword());
        return creatablePersonOptional == null;
    }
}
