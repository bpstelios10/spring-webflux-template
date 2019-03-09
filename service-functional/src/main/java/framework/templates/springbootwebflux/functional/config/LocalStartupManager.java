package framework.templates.springbootwebflux.functional.config;

import framework.templates.springbootwebflux.service.SpringBootWebfluxApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static java.lang.System.exit;

@Configuration
@Profile("!remote")
@Slf4j
public class LocalStartupManager {

    private ConfigurableApplicationContext serviceApplicationContext;

    @PostConstruct
    private void startup() {
        try {
            log.debug("starting server from functional tests");
            serviceApplicationContext = SpringApplication.run(SpringBootWebfluxApplication.class);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            log.error("***** Failed to start the application server!! *****");
            log.error("Exception Thrown: ", e);
            exit(1);
        }
    }

    @PreDestroy
    void shutdown() {
        log.debug("stopping server from functional tests");
        serviceApplicationContext.close();
    }
}
