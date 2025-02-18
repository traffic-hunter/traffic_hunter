package ygo.traffic_hunter.core.alarm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.alarm.message.Message;

@Component
@RequiredArgsConstructor
public class AlarmManager {

    private final List<AlarmSender> alarmSenders;

    public void send(final Message message) {

        for (AlarmSender alarmSender : alarmSenders) {
            alarmSender.send(message);
        }
    }
}
