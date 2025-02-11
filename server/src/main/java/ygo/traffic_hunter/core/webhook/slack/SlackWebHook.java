package ygo.traffic_hunter.core.webhook.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.webhook.message.Message;
import ygo.traffic_hunter.core.webhook.property.WebHookProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class SlackWebHook implements AlarmSender {

    private final Slack slack;

    private final WebHookProperties properties;

    @Override
    public void send(final Message message) {

        try {
            slack.send(properties.slackUrl(), Payload.builder()
                            .text(message.getContent())
                    .build());
        } catch (IOException e) {
            throw new AlarmException(e.getMessage(), e);
        }
    }
}
