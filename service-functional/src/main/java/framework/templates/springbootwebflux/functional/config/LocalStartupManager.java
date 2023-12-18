package framework.templates.springbootwebflux.functional.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import framework.templates.springbootwebflux.service.SpringBootWebfluxApplication;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static java.lang.System.exit;

@Slf4j
@Configuration
@Profile("!remote")
public class LocalStartupManager {

    private ConfigurableApplicationContext serviceApplicationContext;

    @PostConstruct
    private void startup() {
        try {
            log.debug("starting server from functional tests");
            serviceApplicationContext = SpringApplication.run(SpringBootWebfluxApplication.class);
        } catch (Exception e) {
            log.error("***** Failed to start the application server from fts *****");
            log.error("Exception Thrown with message [{}]: ", e.getMessage(), e);
            exit(1);
        }
    }

    @PreDestroy
    void shutdown() {
        log.debug("stopping server from functional tests");
        serviceApplicationContext.close();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer wireMockServer(@Value("${wiremock.port}") int port) {
        return new WireMockServer(WireMockConfiguration.wireMockConfig()
                .notifier(new ConsoleNotifier(true))
                .port(port)
                .mappingSource(new JsonFileMappingsSource(new ClasspathFileSource("mappings"), null))
        );
    }
}
