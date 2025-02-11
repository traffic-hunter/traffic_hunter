package ygo.traffic_hunter.core.webhook.discord;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.webhook.message.Message;
import ygo.traffic_hunter.core.webhook.property.WebHookProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordWebHook implements AlarmSender {

    private final RestClient restClient;

    private final WebHookProperties properties;

    @Override
    public void send(final Message message) {

        restClient.post()
                .uri(URI.create(properties.discordUrl()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(message)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, ((request, response) ->
                        log.info("status code = {} status = {}", response.getStatusCode(), response.getStatusText())))
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->
                        log.error("status code = {} status = {}", response.getStatusCode(), response.getStatusText()))
                .toBodilessEntity();
    }
}
