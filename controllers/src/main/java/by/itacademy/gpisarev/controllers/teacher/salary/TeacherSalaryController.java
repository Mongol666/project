package by.itacademy.gpisarev.controllers.teacher.salary;

import by.itacademy.gpisarev.controllers.AbstractController;
import by.itacademy.gpisarev.secondary.Salary;
import by.itacademy.gpisarev.users.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("teacher/salary")
public class TeacherSalaryController extends AbstractController {

    private ModelAndView getAllTeacherSalaries(HttpSession session, String message) {
        ModelAndView modelAndView = new ModelAndView();
        Teacher teacher = (Teacher) session.getAttribute("user");
        if (teacher == null) {
            modelAndView.setViewName("/teacher");
            log.error("Учителя нет в сессии, необходимо авторизоваться");
            return modelAndView;
        }

        modelAndView.setViewName("/teacher_salaries");
        modelAndView.getModel().put("teacher", teacher);
        modelAndView.getModel().put("messageFromSalary", message);
        return modelAndView;
    }

    @GetMapping
    public ModelAndView get(HttpSession session) {
        return getAllTeacherSalaries(session, "Все зарплаты");
    }

    @GetMapping("/average")
    public ModelAndView getWithAverage(HttpSession session) {
        Teacher teacher = (Teacher) session.getAttribute("user");
        double averageSalary = teacher.getSalaries().stream().mapToInt(Salary::getSalary).average().orElse(0.0);
        return getAllTeacherSalaries(session, String.valueOf(averageSalary));
    }
}
