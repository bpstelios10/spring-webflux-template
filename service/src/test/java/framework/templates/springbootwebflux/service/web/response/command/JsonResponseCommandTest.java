package framework.templates.springbootwebflux.service.web.response.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import framework.templates.springbootwebflux.service.web.domain.QuoteResponse;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class JsonResponseCommandTest {
    private JsonResponseCommand jsonResponseCommand;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static Stream<Arguments> quoteValues() {
        return Stream.of(
                Arguments.of("text", "{\"quote\":\"text\"}"),
                Arguments.of("text1", "{\"quote\":\"text1\"}"),
                Arguments.of("", "{\"quote\":\"No quotes were found\"}"),
                Arguments.of(null, "{\"quote\":\"No quotes were found\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("quoteValues")
    void execute_succeeds(String quote, String responseBody) {
        jsonResponseCommand = new JsonResponseCommand(Mono.just(QuoteResponse.of(quote)), objectMapper);

        StepVerifier.create(jsonResponseCommand.execute())
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(APPLICATION_JSON);
                    assertThat(((EntityResponse) serverResponse).entity()).isEqualTo(responseBody.getBytes());
                })
                .verifyComplete();
    }
}