package servlets.person;

import by.itacademy.pisarev.person.PersonRepository;
import credentials.Credentials;
import lombok.extern.slf4j.Slf4j;
import role.Role;
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
import java.util.stream.Collectors;

@Slf4j
@WebServlet("/TeacherServlet")
public class TeacherServlet extends AbstractServlet {

    private Set<Person> getAllSTeachers() {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        return personRepository.getAllPersons()
                .stream()
                .filter(person -> person.getRole() == Role.TEACHER)
                .collect(Collectors.toSet());
    }

    private void getAllTeachersForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Set<Person> teachers = getAllSTeachers();
        request.setAttribute("teachers", teachers);
        forward("/admin_teacher.jsp", request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllTeachersForward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получение новых данных о пользователе (учителе) и его создание");
        Person newTeacher = new Teacher()
                .withLastName(req.getParameter("newLastName"))
                .withFirstName(req.getParameter("newFirstName"))
                .withPatronymic(req.getParameter("newPatronymic"))
                .withCredentials(new Credentials()
                        .withLogin(req.getParameter("newLogin"))
                        .withPassword(req.getParameter("newPassword")))
                .withDateOfBirth(LocalDate.parse(req.getParameter("newDateOfBirth")));

        if (!checkTeacherInRepository(personRepository, newTeacher)) {
            log.info("Создание учителя");
            if (personRepository.createPerson(newTeacher)) {
                log.info("Учитель создан");
            } else {
                log.error("Учитель не создан");
            }
        } else {
            String messageAboutNotCreating = "Учитель с введёнными учётными данными уже существует";
            req.setAttribute("message", messageAboutNotCreating);
            getAllTeachersForward(req, resp);
            return;
        }
        getAllTeachersForward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        log.info("Получения ID пользователя (учителя) для обновления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.info("Получение ID учётных данных (учитель) для обновления");
        int credentialID = Integer.parseInt(req.getParameter("credentialID"));

        Person updatableTeacher = personRepository.getPersonById(id);
        if (updatableTeacher == null) {
            String messageAboutNotUpdating = "Учитель не обновлён";
            req.setAttribute("message", messageAboutNotUpdating);
            getAllTeachersForward(req, resp);
        } else {
            if (updatableTeacher.getRole() == Role.TEACHER) {
                log.info("Обновление учителя");
                updateTeacher(req, personRepository, id, credentialID);
            } else {
                log.error("Учитель не обновлен");
                getAllTeachersForward(req, resp);
            }
        }
        getAllTeachersForward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        log.info("Получения ID пользователя (учителя) для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));
        Person removableTeacher = personRepository
                .getPersonById(id);
        if (removableTeacher != null) {
            log.info("Удаление учителя");
            if (personRepository.deletePersonById(id)) {
                log.info("Учитель удален");
            } else {
                log.error("Учитель не удален");
            }
        } else {
            String messageAboutNotDeleting = "Учитель не удалён";
            req.setAttribute("message", messageAboutNotDeleting);
        }
        getAllTeachersForward(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        switch (method) {
            case "put":
                doPut(req, resp);
                break;
            case "delete":
                doDelete(req, resp);
                break;
            case "get":
                doGet(req, resp);
                break;
            case "post":
                doPost(req, resp);
                break;
            default:
                super.service(req, resp);
        }
    }

    private void updateTeacher(HttpServletRequest req, PersonRepository personRepository, int id, int credentialID) {
        log.info("Получение новых фамилии, имени и отчества пользователя (учителя) для обновления");
        String newFirstName = req.getParameter("newFirstName");
        String newLastName = req.getParameter("newLastName");
        String newPatronymic = req.getParameter("newPatronymic");

        log.info("Получение новых логина и пароля пользователя (учителя) для обновления");
        String newLogin = req.getParameter("newLogin");
        String newPassword = req.getParameter("newPassword");

        log.info("Получение новой даты рождения пользователя (учителя) для обновления");
        String newDateOfBirthString = req.getParameter("newDateOfBirth");
        LocalDate newDateOfBirth = LocalDate.parse(newDateOfBirthString);

        Teacher newTeacher = new Teacher()
                .withFirstName(newFirstName)
                .withLastName(newLastName)
                .withPatronymic(newPatronymic)
                .withDateOfBirth(newDateOfBirth)
                .withCredentials(new Credentials()
                        .withLogin(newLogin)
                        .withPassword(newPassword));
        newTeacher.setId(id);
        newTeacher.getCredentials().setId(credentialID);

        if (personRepository.updateAllPersonProperties(newTeacher)) {
            log.info("Учитель обновлён");
        } else {
            log.error("Учитель не обновлён");
        }
    }

    private boolean checkTeacherInRepository(PersonRepository personRepository, Person newTeacher) {
        Person creatableTeacher = personRepository.getPersonByName(newTeacher.getFirstName(), newTeacher.getLastName(), newTeacher.getPatronymic());
        if (creatableTeacher != null) {
            creatableTeacher = personRepository.getPersonByCredentials(newTeacher.getCredentials().getLogin(), newTeacher.getCredentials().getPassword());
            return creatableTeacher != null;
        }
        return false;
    }
}
