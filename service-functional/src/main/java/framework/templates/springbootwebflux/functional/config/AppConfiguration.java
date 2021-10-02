package framework.templates.springbootwebflux.functional.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.functional.client.ServiceRequestGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

@Slf4j
@Configuration
public class AppConfiguration {

    private static final String DEFAULT_CONFIG_SUFFIX = "local";

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer properties(Environment environment) {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setProperties(getPropertiesFromApplicationYaml(environment));

        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public ServiceRequestGenerator serviceRequestGenerator(@Value("${service.scheme}") String scheme,
                                                           @Value("${service.host}") String host,
                                                           @Value("${service.port}") Integer port,
                                                           @Value("${service.contextPath}") String contextPath) {
        return new ServiceRequestGenerator(scheme, host, port, contextPath);
    }

    private Properties getPropertiesFromApplicationYaml(Environment environment) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        String applicationYamlFileName = "application-" + configSuffix(environment) + ".yml";
        yaml.setResources(new ClassPathResource(applicationYamlFileName));

        return ofNullable(yaml.getObject())
                .orElseThrow(() ->
                        new RuntimeException("Application yaml file missing: " + applicationYamlFileName)
                );
    }

    private static String configSuffix(Environment environment) {
        return stream(environment.getActiveProfiles()).findFirst().orElse(DEFAULT_CONFIG_SUFFIX);
    }
}
