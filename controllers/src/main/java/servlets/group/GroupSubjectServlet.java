package servlets.group;

import by.itacademy.pisarev.group.GroupRepository;
import by.itacademy.pisarev.subject.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import secondary.Group;
import secondary.Subject;
import servlets.AbstractServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
@WebServlet("/GroupSubjectServlet")
public class GroupSubjectServlet extends AbstractServlet {

    private Group getGroupByID(HttpServletRequest request) {
        GroupRepository groupRepository = (GroupRepository) getServletContext().getAttribute("group_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        int groupID = Integer.parseInt(request.getParameter("groupID"));
        Group group = groupRepository.getGroupById(groupID);
        if (group != null) {
            Set<Subject> subjectsInGroup = subjectRepository.getSubjectsByGroupID(groupID);
            group.setSubjects(subjectsInGroup);
            return group;
        } else {
            log.error("Группа не найдена.");
            return null;
        }
    }

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

    private Subject getSubjectByID(HttpServletRequest request) {
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");
        int subjectID = Integer.parseInt(request.getParameter("subjectID"));
        Subject subject = subjectRepository.getSubjectById(subjectID);
        if (subject != null) {
            return subject;
        } else {
            log.error("Предмет не найден");
            return null;
        }
    }

    private Subject getSubjectByName(HttpServletRequest request) {
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");
        String subjectName = request.getParameter("newName");
        Subject subject = subjectRepository.getSubjectByName(subjectName);
        if (subject != null) {
            return subject;
        } else {
            log.error("Предмет не найден");
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
        Subject subject = getSubjectByName(req);

        if (group != null) {
            if (subject != null) {
                if (groupRepository.updateSubjectsAdd(group, subject)) {
                    log.info("Предмет добавлен в группу");
                } else {
                    log.error("Предмет не добавлен в группу");
                }
            } else {
                log.error("Предмет не найден");
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
        Subject subject = getSubjectByID(req);

        if (group != null) {
            if (subject != null) {
                if (groupRepository.updateSubjectsRemove(group, subject)) {
                    log.info("Предмет удален из группы");
                } else {
                    log.error("Предмет не удален из группы");
                }
            } else {
                log.error("Предмет не найден");
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
