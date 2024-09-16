package servlets.mark;

import by.itacademy.pisarev.mark.MarkRepository;
import by.itacademy.pisarev.person.PersonRepository;
import by.itacademy.pisarev.subject.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import role.Role;
import secondary.Mark;
import secondary.Subject;
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

@Slf4j
@WebServlet("/MarksServlet")
public class MarksServlet extends AbstractServlet {

    private void getAllStudentMarks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");

        Student student = getStudentByID(request);
        if (student != null) {
            Set<Mark> marks = markRepository.getMarksByStudentID(student.getId());
            student.setMarks(marks);
            request.setAttribute("student", student);
            forward("/admin_students_marks.jsp", request, response);
        } else {
            log.error("Возврат");
            forward("/admin_student.jsp", request, response);
        }
    }

    private Student getStudentByID(HttpServletRequest request) {
        PersonRepository personRepository = (PersonRepository) getServletContext().getAttribute("person_repository");

        Person person = personRepository
                .getPersonById(Integer.parseInt(request.getParameter("studentID")));
        if (person != null) {
            if (person.getRole() == Role.STUDENT) {
                return (Student) person;
            } else {
                log.error("{} не является студентом.", person);
                return null;
            }
        } else {
            log.error("Студент не найден.");
            return null;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllStudentMarks(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Получение данных для создания оценки");
        int studentID = Integer.parseInt(req.getParameter("studentID"));
        int count = Integer.parseInt(req.getParameter("newMark"));
        LocalDate date = LocalDate.parse(req.getParameter("newDate"));
        String subjectName = req.getParameter("newSubjectName");

        log.info("Поиск студента по ID для присвоения оценки");
        Student student = getStudentByID(req);
        if (student != null) {
            log.info("Студент найден");
        } else {
            getAllStudentMarks(req, resp);
        }

        log.info("Проверяется, есть ли введённый предмет");
        Subject subject = subjectRepository.getSubjectByName(subjectName);
        if (subject != null) {
            log.info("Создание оценки");
            Mark mark = new Mark()
                    .withMark(count)
                    .withDateOfMark(date)
                    .withSubject(subject);
            if (markRepository.createMark(mark, studentID)) {
                log.info("Оценка добавлена");
            } else {
                log.error("Оценка не добавлена");
            }
        } else {
            log.error("Предмет не найден");
        }
        getAllStudentMarks(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");
        SubjectRepository subjectRepository = (SubjectRepository) getServletContext().getAttribute("subject_repository");

        log.debug("Получение новых данных об оценке");
        int markID = Integer.parseInt(req.getParameter("ID"));

        Mark mark = markRepository.getMarkByID(markID);
        if (mark == null) {
            req.setAttribute("markNotFound", "Оценка не найдена");
            getAllStudentMarks(req, resp);
        }

        int newCount = Integer.parseInt(req.getParameter("newMark"));
        LocalDate newDate = LocalDate.parse(req.getParameter("newDate"));
        String newSubjectName = req.getParameter("newSubjectName");

        log.info("Проверяется, есть ли введённый предмет");
        Subject subject = subjectRepository.getSubjectByName(newSubjectName);
        if (subject != null && mark != null) {
            log.info("Обновление оценки");
            mark.setMark(newCount);
            mark.setDateOfMark(newDate);
            mark.setSubject(subject);
            if (markRepository.updateMark(mark)) {
                log.info("Оценка обновлена");
            } else {
                log.error("Оценка не обновлена");
            }
        } else {
            log.error("Предмет не найден");
        }
        getAllStudentMarks(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        MarkRepository markRepository = (MarkRepository) getServletContext().getAttribute("mark_repository");
        log.info("Получение ID для удаления оценки");
        int id = Integer.parseInt(req.getParameter("ID"));

        log.debug("Удаление оценки");
        if (markRepository.deleteMarkById(id)) {
            log.info("Оценка удалена");
        } else {
            log.error("Оценка не удалена");
        }

        getAllStudentMarks(req, resp);
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