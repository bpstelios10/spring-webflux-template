package framework.templates.springbootwebflux.service.clients.rest;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class WebClientFactory {

    private final WebClientProperties webClientProperties;

    public WebClientFactory(WebClientProperties webClientProperties) {
        this.webClientProperties = webClientProperties;
    }

    public WebClient get(String poolName) {
        ConnectionProvider connectionProvider =
                ConnectionProvider.builder(poolName)
                        .maxConnections(webClientProperties.getPoolSize())
                        .pendingAcquireTimeout(Duration.ofMillis(webClientProperties.getConnectionRequestTimeoutMs()))
                        .build();

        TcpClient tcpClient = TcpClient
                .create(connectionProvider)
                .option(CONNECT_TIMEOUT_MILLIS, webClientProperties.getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(webClientProperties.getReadTimeoutMs(), MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(1000, MILLISECONDS));
                });

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }
}
