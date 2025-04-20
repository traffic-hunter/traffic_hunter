package ygo.traffic_hunter.common.web.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import ygo.traffic_hunter.AbstractTestConfiguration;

/**
 * @author JuSeong
 * @version 1.1.0
 */
class RequestPatternTest extends AbstractTestConfiguration {

    @Test
    void 요청_주소가_일치한다면_True() {

        // given
        RequestPattern requestPattern = new RequestPattern("/members/**", HttpMethod.POST);

        //when & then
        Assertions.assertThat(requestPattern.match("/members/check", HttpMethod.POST)).isTrue();
    }

    @Test
    void 요청_주소가_일치하지않으면_False() {

        // given
        RequestPattern requestPattern = new RequestPattern("/members/**", HttpMethod.POST);

        //when & then
        Assertions.assertThat(requestPattern.match("/metrics/check", HttpMethod.POST)).isFalse();
    }
}