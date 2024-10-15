package ygo.traffichunter.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ygo.TestExt;

class AgentUtilTest extends TestExt {

    @ParameterizedTest
    @ValueSource(strings = {
            "127.0.0.1:8080",
            "localhost:7000",
            "999.999.999.999:56646",
            "192.168.0.1:9000"
    })
    void ip_port_형식이_맞는지_확인한다_다만_localhost는_허용한다(final String addr) {
        // given

        // when
        boolean isAddr = AgentUtil.isAddr(addr);

        // then
        assertTrue(isAddr);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "127.0.1:8080",
            "localhost:700000",
            "999.999.999.999.999:56646",
            "192.168.099999.10000:9000",
            "",
            ":8080",
            "addr:9000"
    })
    void 잘못된_ip_port_형식이_들어오는_경우를_확인한다(final String addr) {
        // given

        // when
        boolean isAddr = AgentUtil.isAddr(addr);

        // then
        assertFalse(isAddr);
    }

    @Test
    void websocket_url_이_정상적으로_구성되는지_확인한다() {
        // given
        String serverUrl = "localhost:8080";
        String res = String.format("ws://%s/traffic-hunter", serverUrl);

        // when
        String websocketUrl = AgentUtil.WEBSOCKET_URL.getUrl(serverUrl);

        // then
        assertEquals(res, websocketUrl);
    }
}