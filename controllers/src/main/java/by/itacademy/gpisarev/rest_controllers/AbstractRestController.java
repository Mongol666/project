package by.itacademy.gpisarev.rest_controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:repository.properties")
public abstract class AbstractRestController {
    protected static final String REPO_SUFFIX = "Impl";

    @Value("${type0}")
    protected String type;
}
