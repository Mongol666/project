package by.itacademy.gpisarev.controllers.student.mark;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.secondary.Mark;
import by.itacademy.gpisarev.users.Student;
import by.itacademy.gpisarev.users.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("student/marks")
public class StudentMarkController extends AbstractController {

    private ModelAndView getAllStudentMarks(HttpSession session, String message) {
        ModelAndView modelAndView = new ModelAndView();
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            modelAndView.setViewName("/student");
            log.error("Студента нет в сессии, необходимо авторизоваться");
            return modelAndView;
        }

        modelAndView.setViewName("/student_marks");
        modelAndView.getModel().put("student", student);
        modelAndView.getModel().put("messageFromMarks", message);
        return modelAndView;
    }

    @GetMapping
    public ModelAndView get(HttpSession session) {
        return getAllStudentMarks(session, "Все оценки");
    }

    @GetMapping("/average")
    public ModelAndView getWithAverage(HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        double averageSalary = student.getMarks().stream().mapToInt(Mark::getMark).average().orElse(0.0);
        return getAllStudentMarks(session, String.valueOf(averageSalary));
    }
}
