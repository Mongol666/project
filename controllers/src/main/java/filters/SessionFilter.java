package filters;

import lombok.extern.slf4j.Slf4j;
import users.Person;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@WebFilter("/index.jsp")
public class SessionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            log.debug("Сессия существует и в сессии есть пользователь");
            Person person = (Person) session.getAttribute("user");

            switch (person.getClass().getName()) {
                case "Student":
                    log.info("Пользователь - студент");
                    req.getRequestDispatcher("/student.jsp").forward(req, resp);
                    break;
                case "Teacher":
                    log.info("Пользователь - учитель");
                    req.getRequestDispatcher("/teacher.jsp").forward(req, resp);
                    break;
                case "Admin":
                    log.info("Пользователь - админ");
                    req.getRequestDispatcher("/admin.jsp").forward(req, resp);
                    break;
                default:
                    log.info("Ошибка определения роли - переход на начальную страницу");
                    session.invalidate();
                    req.getRequestDispatcher("/index.jsp").forward(req, resp);
            }
        } else {
            log.info("Сессии не существует или пользователя нет в сессии. Необходимо аутентифицироваться");
            filterChain.doFilter(req, resp);
        }
    }
}
