package filters;

import credentials.Credentials;
import by.itacademy.pisarev.fabric.RepositoryFactory;
import by.itacademy.pisarev.fabric.RepositoryFactoryJpaImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import users.Admin;
import users.Person;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ResourceBundle;

@WebFilter(filterName = "InitializeRepository")
@Slf4j
public class InitializeRepositoryFilter implements Filter {
    private final ResourceBundle bundle = ResourceBundle.getBundle("repository");

    @SneakyThrows
    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("Получение типа репозитория");
        String typeOfRepository = bundle.getString("type0");

        if ("hibernate".equals(typeOfRepository)) {
            log.info("Тип: Hibernate");
            loadRepositoryHibernate(filterConfig);
        }
    }



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private void loadRepositoryHibernate(FilterConfig filterConfig) {
        Configuration configuration = new Configuration().configure();
        try {
            SessionFactory factory = configuration.buildSessionFactory();
            RepositoryFactory repositoryFactory = RepositoryFactoryJpaImpl.getInstance(factory);
            setRepositoriesToContext(filterConfig, repositoryFactory);
            setValuesToRepositories(repositoryFactory);
        } catch (Exception e) {
            log.error("Не удалось загрузить тип Hibernate: " + e.getMessage());
        }

    }

    private void setRepositoriesToContext(FilterConfig config, RepositoryFactory factory) {
        log.info("Загрузка репозиториев в Context");
        ServletContext context = config.getServletContext();
        context.setAttribute("credential_repository", factory.getCredentialRepository());
        context.setAttribute("group_repository", factory.getGroupRepository());
        context.setAttribute("mark_repository", factory.getMarkRepository());
        context.setAttribute("person_repository", factory.getPersonRepository());
        context.setAttribute("salary_repository", factory.getSalaryRepository());
        context.setAttribute("subject_repository", factory.getSubjectRepository());
    }

    private void setValuesToRepositories(RepositoryFactory factory) {
        log.info("Добавление админа");
        Person admin = new Admin()
                .withFirstName("Someone")
                .withLastName("Anon")
                .withPatronymic("No")
                .withCredentials(new Credentials()
                        .withLogin("Anonymous")
                        .withPassword("Anonim"))
                .withDateOfBirth(LocalDate.of(1, Month.MARCH, 1));
        factory.getPersonRepository().createPerson(admin);

    }
}
