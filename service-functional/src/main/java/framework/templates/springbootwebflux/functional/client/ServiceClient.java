package framework.templates.springbootwebflux.functional.client;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

@Component
public class ServiceClient {

    @Getter
    private ServiceResponse response;

    public void execute(ServiceRequest serviceRequest) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(toHttpUriRequest(serviceRequest));

            this.response = toServiceResponse(response);
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

        final RequestBuilder requestBuilder = RequestBuilder
                .create(serviceRequest.getMethod())
                .setUri(uri)
                .setEntity(EntityBuilder.create().setText(serviceRequest.getBody()).build());

        emptyIfNull(serviceRequest.getHeaders()).forEach(requestBuilder::addHeader);

        return requestBuilder.build();
    }

    private ServiceResponse toServiceResponse(HttpResponse response) throws IOException {
        return ServiceResponse.builder()
                .headers(
                        stream(response.getAllHeaders())
                                .collect(Collectors.toMap(Header::getName, Header::getValue))
                )
                .body(EntityUtils.toString(response.getEntity()))
                .statusCode(response.getStatusLine().getStatusCode())
                .statusLine(response.getStatusLine().getReasonPhrase())
                .build();
    }
}
