package filters;

import lombok.extern.slf4j.Slf4j;

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
@WebFilter(urlPatterns = {
        "/GroupServlet",
        "/GroupStudentServlet",
        "/GroupSubjectServlet",
        "/MarksServlet",
        "/SalaryServlet",
        "/StudentServlet",
        "/SubjectServlet",
        "/TeacherServlet",
        "/admin.jsp",
        "/admin_groups.jsp",
        "/admin_salary.jsp",
        "/admin_student.jsp",
        "/admin_students_marks.jsp",
        "/admin_subjects.jsp",
        "/admin_subjects_students_in_group.jsp",
        "/admin_teacher.jsp"
},
    filterName = "CheckSession")
public class CheckSessionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpSession session = httpReq.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            log.debug("Пользователя нет в сессии или сессия не существует. Переход на начальную страницу");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } else {
            log.info("Пользователь есть в сессии и сессия существует. Продолжается работа");
            chain.doFilter(request, response);
        }
    }
}
