package framework.templates.springbootwebflux.functional;

public class CucumberTest {

    public static void main(String... args) {
        String[] newArgs = new String[]{
                "--tags", "~@wip",
                "--plugin", "pretty",
                "--plugin", "html:build/cucumber-report",
                "--plugin", "junit:build/junit-test-report.xml",
                "--plugin", "json:build/cucumber-report.json",
                "--glue", "framework.templates.springbootwebflux.functional.steps",
                "service-functional/src/main/resources/features/"
        };

        io.cucumber.core.cli.Main.main(newArgs);
    }
}
