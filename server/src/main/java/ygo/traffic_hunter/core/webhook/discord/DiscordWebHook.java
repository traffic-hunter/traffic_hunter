package ygo.traffic_hunter.core.webhook.discord;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ygo.traffic_hunter.core.send.Sender;
import ygo.traffic_hunter.core.webhook.property.WebHookProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebHook implements Sender {

    private final RestClient restClient;

    private final WebHookProperties properties;

    @Override
    public <T> void send(final T data) {

        restClient.post()
                .uri(URI.create(properties.discordUrl()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(data)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) ->
                        log.info("status code = {} status = {}", response.getStatusCode(), response.getStatusText())))
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        log.error("status code = {} status = {}", response.getStatusCode(), response.getStatusText()))
                .toBodilessEntity();
    }

    @Override
    public <T> void send(final List<T> data) {

        restClient.post()
                .uri(URI.create(properties.discordUrl()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(data)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) ->
                        log.info("status code = {} status = {}", response.getStatusCode(), response.getStatusText())))
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        log.error("status code = {} status = {}", response.getStatusCode(), response.getStatusText()))
                .toBodilessEntity();
    }
}
