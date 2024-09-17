package by.itacademy.gpisarev.configuration;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.beans.PropertyVetoException;

@ComponentScan("by.itacademy.gpisarev")
@Configuration
@EnableWebMvc
@PropertySource("classpath:repository.properties")
public class ApplicationConfiguration {
    @Value("${driver}")
    private String driver;

    @Value("${url}")
    private String url;

    @Value("${login}")
    private String user;

    @Value("${password}")
    private String password;

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver(@Autowired ApplicationContext context) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setApplicationContext(context);
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public ComboPooledDataSource dataSource() throws PropertyVetoException {
        ComboPooledDataSource pooledDataSource = new ComboPooledDataSource();
        pooledDataSource.setDriverClass(driver);
        pooledDataSource.setJdbcUrl(url);
        pooledDataSource.setUser(user);
        pooledDataSource.setPassword(password);
        pooledDataSource.setInitialPoolSize(10);
        pooledDataSource.setAcquireIncrement(2);
        pooledDataSource.setMaxPoolSize(15);
        pooledDataSource.setMinPoolSize(2);
        pooledDataSource.setMaxStatements(100);
        return pooledDataSource;
    }

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.addAnnotatedClass(by.itacademy.gpisarev.users.Student.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.users.Admin.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.users.Teacher.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.secondary.Group.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.secondary.Salary.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.secondary.Mark.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.secondary.Subject.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.credentials.Credentials.class);
        configuration.addAnnotatedClass(by.itacademy.gpisarev.users.Person.class);
        return configuration.configure().buildSessionFactory();
    }

}
