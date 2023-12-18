package framework.templates.springbootwebflux.functional.client;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
public class ServiceClient {

    @Getter
    private ServiceResponse response;

    public void execute(ServiceRequest serviceRequest) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            response = httpClient.execute(toHttpUriRequest(serviceRequest), this::toServiceResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private HttpUriRequest toHttpUriRequest(ServiceRequest serviceRequest) {
        URI uri = new URIBuilder()
                .setScheme(serviceRequest.getScheme())
                .setHost(serviceRequest.getHost())
                .setPort(serviceRequest.getPort())
                .setPath(serviceRequest.getPath())
                .build();

        HttpGet httpGet = new HttpGet(uri); // TODO handle POST too
        getHeadersOrEmptyIfNull(serviceRequest).forEach(httpGet::addHeader);

        return httpGet;
    }

    private ServiceResponse toServiceResponse(ClassicHttpResponse response) throws IOException, ParseException {
        return ServiceResponse.builder()
                .headers(
                        stream(response.getHeaders())
                                .collect(Collectors.toMap(Header::getName, Header::getValue))
                )
                .body(EntityUtils.toString(response.getEntity()))
                .statusCode(response.getCode())
                .statusLine(response.getReasonPhrase())
                .build();
    }

    private static Map<String, String> getHeadersOrEmptyIfNull(ServiceRequest serviceRequest) {
        Map<String, String> headers = serviceRequest.getHeaders();
        if (headers == null) headers = Map.of();
        return headers;
    }
}
