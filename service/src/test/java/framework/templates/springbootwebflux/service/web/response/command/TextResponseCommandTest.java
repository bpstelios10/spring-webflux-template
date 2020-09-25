package framework.templates.springbootwebflux.service.web.response.command;

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
import static org.springframework.http.MediaType.TEXT_PLAIN;

@ExtendWith(MockitoExtension.class)
class TextResponseCommandTest {
    private TextResponseCommand textResponseCommand;

    private static Stream<Arguments> quoteValues() {
        return Stream.of(
                Arguments.of("text", "text"),
                Arguments.of("text1", "text1"),
                Arguments.of("", "No quotes were found"),
                Arguments.of(null, "No quotes were found")
        );
    }

    @ParameterizedTest
    @MethodSource("quoteValues")
    void execute_succeeds(String quote, String responseBody) {
        textResponseCommand = new TextResponseCommand(Mono.just(QuoteResponse.of(quote)));

        StepVerifier.create(textResponseCommand.execute())
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(OK);
                    assertThat(serverResponse.headers().getContentType()).isEqualTo(TEXT_PLAIN);
                    assertThat(((EntityResponse) serverResponse).entity()).isEqualTo(responseBody);
                })
                .verifyComplete();
    }
}