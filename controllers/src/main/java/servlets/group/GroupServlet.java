package servlets.group;

import by.itacademy.pisarev.group.GroupRepository;
import by.itacademy.pisarev.person.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import role.Role;
import secondary.Group;
import servlets.AbstractServlet;
import users.Person;
import users.Teacher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
@WebServlet("/GroupServlet")
public class GroupServlet extends AbstractServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        Group group;

        log.info("Получение данных о новой группе");
        String name = req.getParameter("newName");
        String lastFirstPatronymicOfTeacher = req.getParameter("lastFirstPatronymic");

        String lastNameOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[0];
        String firstNameOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[1];
        String patronymicOfTeacher = lastFirstPatronymicOfTeacher.split(" ")[2];

        log.debug("Проверка, есть ли такой учитель");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Person teacher = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);

        if (teacher != null) {
            if (teacher.getRole() == Role.TEACHER) {
                group = new Group()
                        .withName(name)
                        .withTeacher((Teacher) teacher);
            } else {
                group = new Group()
                        .withName(name);
            }
        } else {
            group = new Group()
                    .withName(name);
        }

        log.info("Создание группы");
        if (groupRepository.createGroup(group)) {
            log.info("Группа создана");
        } else {
            log.error("Группа не создана");
        }

        getAllGroupsForward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");

        log.debug("Получение новых данных о группе");
        int id = Integer.parseInt(req.getParameter("ID"));

        Group group = groupRepository.getGroupById(id);
        if (group == null) {
            req.setAttribute("groupNotFound", "Группа не найдена");
            getAllGroupsForward(req, resp);
        }

        String newName = req.getParameter("newName");
        String newLastFirstPatronymicOfTeacher = req.getParameter("newLastFirstPatronymic");

        String lastNameOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[0];
        String firstNameOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[1];
        String patronymicOfTeacher = newLastFirstPatronymicOfTeacher.split(" ")[2];

        log.debug("Проверка, есть ли учитель, который будет вести эту группу");
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");
        Person newTeacher = personRepository.getPersonByName(firstNameOfTeacher, lastNameOfTeacher, patronymicOfTeacher);
        if (newTeacher != null && group != null && newTeacher.getRole() == Role.TEACHER) {
            group.setName(newName);
            group.setTeacher((Teacher) newTeacher);
            if (groupRepository.updateGroup(group)) {
                log.info("Группа обновлена");
            } else {
                log.error("Группа не обновлена");
            }
        } else {
            log.error("Учитель не найден");
        }

        getAllGroupsForward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        log.info("Получение ID группы для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Удаление группы");
        if (groupRepository.deleteGroupById(id)) {
            log.info("Группа удалена");
        } else {
            log.error("Группа не удалена");
        }

        getAllGroupsForward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllGroupsForward(req, resp);
    }

    private void getAllGroupsForward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        Set<Group> groups = groupRepository.getAllGroups();
        request.setAttribute("groups", groups);
        forward("/admin_groups.jsp", request, response);
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
