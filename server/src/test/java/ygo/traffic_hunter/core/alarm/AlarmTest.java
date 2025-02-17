package ygo.traffic_hunter.core.alarm;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.send.AlarmSender;

@SpringBootTest
public class AlarmTest extends AbstractTestConfiguration {

    @Autowired
    private List<AlarmSender> alarmSenders;

    @Test
    void 알람을_보낼_Sender가_몇개인지_확인한다() {
        // given

        // when
        int size = alarmSenders.size();

        // then
        System.out.println(size);
    }
}
