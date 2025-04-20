package ygo.traffic_hunter.core.sse;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.alarm.loss.LossPreventionHooker;
import ygo.traffic_hunter.core.alarm.message.Message;
import ygo.traffic_hunter.core.repository.AlarmRepository;
import ygo.traffic_hunter.core.repository.MemberRepository;
import ygo.traffic_hunter.domain.entity.user.Member;
import ygo.traffic_hunter.domain.entity.user.Role;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

/**
 * @author JuSeong1130
 * @version 1.1.0
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ServerSentEventManagerTest extends AbstractTestConfiguration {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LossPreventionHooker lossPreventionHooker;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private ServerSentEventManager serverSentEventManager;

    @Mock
    private Client client;

    @MockitoBean
    private Map<Member, Client> clientMap;

    private Logger log = LoggerFactory.getLogger(ServerSentEventManagerTest.class);


    @AfterEach
    void tearDown() {
        alarmRepository.clearDeadLetter();
        Member findMember = memberRepository.findByEmail("test@test.com");
        memberRepository.delete(findMember);
    }

    @Test
        //@Disabled
    void 전송_예외가_발생하면_DeadLetter가_저장된다() throws IOException, InterruptedException {

        // given
        BDDMockito.given(clientMap.get(BDDMockito.any())).willReturn(client);
        BDDMockito.given(clientMap.containsKey(BDDMockito.any())).willReturn(true);
        BDDMockito.willThrow(ServerSentEventManager.ServerSentEventException.class).given(client).send("data");

        Member member = Member.builder()
                .email("test@test.com")
                .password("test")
                .isAlarm(true)
                .role(Role.USER)
                .build();

        Message message = Message.builder()
                .content("asd")
                .url("asdf")
                .time(Instant.now())
                .username("test")
                .addEmbed(Message.Embed.builder()
                        .color(1)
                        .description("test")
                        .title("asd")
                        .addField(Message.Field.of("test", "test", true))
                        .build())
                .build();

        memberRepository.save(member);

        // when
        serverSentEventManager.send(message);

        // then
        assertThatThrownBy(() -> client.send("data"))
                .isInstanceOf(ServerSentEventManager.ServerSentEventException.class);

        TimeUnit.SECONDS.sleep(1); // 1초 동안 대기
        Assertions.assertThat(alarmRepository.existDeadLetter()).isTrue(); // DeadLetter가 저장되었는지 확인
    }

}
