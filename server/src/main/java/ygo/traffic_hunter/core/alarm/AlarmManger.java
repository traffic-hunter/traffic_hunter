package ygo.traffic_hunter.core.alarm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.webhook.message.Message;

@Component
@RequiredArgsConstructor
public class AlarmManger {

    private final List<AlarmSender> alarmSenders;

    public void send(final Message message) {

        for (AlarmSender alarmSender : alarmSenders) {
            alarmSender.send(message);
        }
    }
}
