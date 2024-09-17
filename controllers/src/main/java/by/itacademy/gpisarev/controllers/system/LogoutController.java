package by.itacademy.gpisarev.controllers.system;

import by.itacademy.gpisarev.users.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("logout")
public class LogoutController {

    @GetMapping
    public ModelAndView get(HttpSession session) {
        Person user = (Person) session.getAttribute("user");
        if (user != null) {
            log.info("Пользователь {} {} {} завершил сеанс", user.getFirstName(), user.getLastName(), user.getPatronymic());
            session.removeAttribute("user");
        }
        session.invalidate();
        log.info("Выход из аккаунта, переход на начальную страницу...");
        return new ModelAndView("/index");
    }
}
