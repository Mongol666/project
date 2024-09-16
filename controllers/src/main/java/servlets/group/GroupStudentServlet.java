package servlets.group;

import by.itacademy.pisarev.group.GroupRepository;
import by.itacademy.pisarev.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import role.Role;
import secondary.Group;
import servlets.AbstractServlet;
import users.Person;
import users.Student;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
@WebServlet("/GroupStudentServlet")
public class GroupStudentServlet extends AbstractServlet {

    private void getGroupForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Group group = getGroupByID(request);

        if (group != null) {
            request.setAttribute("group", group);
            forward("/admin_subjects_students_in_group.jsp", request, response);
        } else {
            log.error("Возврат");
            forward("/admin_groups.jsp", request, response);
        }
    }

    private Group getGroupByID(HttpServletRequest request) {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        int groupID = Integer.parseInt(request.getParameter("groupID"));
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            Set<Student> students = personRepository.getStudentsByGroupID(groupID);
            group.setStudents(students);
            return group;
        } else {
            log.error("Группа не найдена.");
            return null;
        }
    }

    private Student getStudentByID(HttpServletRequest request) {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        int studentID = Integer.parseInt(request.getParameter("studentID"));

        Person person = personRepository.getPersonById(studentID);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                return (Student) person;
            } else {
                log.error("{} не является студентом", person);
                return null;
            }
        } else {
            log.error("Студент не найден");
            return null;
        }
    }

    private Student getStudentByNames(HttpServletRequest request) {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        String lastName = request.getParameter("newLastName");
        String firstName = request.getParameter("newFirstName");
        String patronymic = request.getParameter("newPatronymic");

        Person person = personRepository.getPersonByName(firstName, lastName, patronymic);
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                return (Student) person;
            } else {
                log.error("{} не является студентом", person);
                return null;
            }
        } else {
            log.error("Студент не найден");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getGroupForward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");

        Group group = getGroupByID(req);
        Student student = getStudentByNames(req);
        if (group != null) {
            if (student != null) {
                if (groupRepository.updateStudentsAdd(group, student)) {
                    log.info("Студент добавлен в группу");
                } else {
                    log.error("Студент не добавлен в группу");
                }
            } else {
                log.error("Студент не найден");
            }
        } else {
            log.error("Группа не найдена");
        }

        getGroupForward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");

        Group group = getGroupByID(req);
        Student student = getStudentByID(req);

        if (group != null) {
            if (student != null) {
                if (groupRepository.updateStudentsRemove(group, student)) {
                    log.info("Студент удален из группы");
                } else {
                    log.error("Студент не удален из группы");
                }
            } else {
                log.error("Студент не найден");
            }
        } else {
            log.error("Группа не найдена");
        }

        getGroupForward(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getParameter("method")) {
            case "post":
                doPost(req, resp);
                break;
            case "delete":
                doDelete(req, resp);
                break;
            case "get":
                doGet(req, resp);
                break;
        }
    }
}
