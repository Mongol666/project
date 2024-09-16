package servlets.subject;

import by.itacademy.pisarev.subject.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import secondary.Subject;
import servlets.AbstractServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Slf4j
@WebServlet("/SubjectServlet")
public class SubjectServlet extends AbstractServlet {

    private void getAllSubjectsFroward(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");
        Set<Subject> subjects = subjectRepository.getAllSubjects();
        request.setAttribute("subjects", subjects);
        forward("/admin_subjects.jsp", request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllSubjectsFroward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Получение данных о новом предмете");
        String newName = req.getParameter("newName");

        log.debug("Создание предмета");
        Subject subject = new Subject()
                .withName(newName);
        if (subjectRepository.createSubject(subject)) {
            log.info("Предмет создан");
        } else {
            log.error("Предмет не создан");
        }
        getAllSubjectsFroward(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Получение ID предмета для обновления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Получение новых данных о предмете");
        String newName = req.getParameter("newName");

        Subject subject = subjectRepository.getSubjectById(id);
        if (subject != null) {
            log.debug("Обновление предмета");
            subject.setName(newName);
            if (subjectRepository.updateSubject(subject)) {
                log.info("Предмет обновлён");
            } else {
                log.error("Предмет не обновлен");
            }
        } else {
            req.setAttribute("subjectNotFound", "Предмет не найден");
            getAllSubjectsFroward(req, resp);
        }
        getAllSubjectsFroward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Получение ID предмета для удаления");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Удаление предмета");
        if (subjectRepository.deleteSubjectById(id)) {
            log.info("Предмет удалён");
        } else {
            log.error("Предмет не удалён");
        }

        getAllSubjectsFroward(req, resp);
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
