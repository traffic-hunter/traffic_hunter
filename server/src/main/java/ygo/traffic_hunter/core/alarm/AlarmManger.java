package ygo.traffic_hunter.core.alarm;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.core.send.AlarmSender;
import ygo.traffic_hunter.core.webhook.message.Message;
import ygo.traffic_hunter.domain.entity.alarm.Threshold;
import ygo.traffic_hunter.domain.entity.alarm.Threshold.Calculator;

@Component
@RequiredArgsConstructor
public class AlarmManger {

    private final List<AlarmSender> alarmSenders;

    private Threshold threshold = Threshold.DEFAULT;

    @Cacheable()
    public void sendAlarm(Calculator calculator) {

        // result
        ThresholdResult result = calculator.calculate(threshold);
        List<Message> exceedData = result.getExceededData();

        for (AlarmSender alarmSender : alarmSenders) {
            if (alarmSender.isActive()) {
                for (Data data : exceedData) {
                    alarmSender.send();
                }
            }
        }
    }

    public void updateThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

}
