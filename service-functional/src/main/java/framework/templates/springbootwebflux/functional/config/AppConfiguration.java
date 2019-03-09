package framework.templates.springbootwebflux.functional.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;

import static java.util.Arrays.stream;

@Slf4j
@Configuration
public class AppConfiguration {

    private static final String DEFAULT_CONFIG_SUFFIX = "local";

    @Bean
    public static ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties(Environment environment) {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-" + configSuffix(environment) + ".yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    private static String configSuffix(Environment environment) {
        return stream(environment.getActiveProfiles()).findFirst().orElse(DEFAULT_CONFIG_SUFFIX);
    }
}
