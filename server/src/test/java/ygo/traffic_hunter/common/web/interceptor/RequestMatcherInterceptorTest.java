package ygo.traffic_hunter.common.web.interceptor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import ygo.traffic_hunter.AbstractTestConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JuSeong
 * @version 1.1.0
 */

@ExtendWith(MockitoExtension.class)
class RequestMatcherInterceptorTest extends AbstractTestConfiguration {

    @Mock
    private LoginInterceptor loginInterceptor;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Object handler;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/test");
        response = new MockHttpServletResponse();
        handler = new Object();
    }

    @Test
    void True() throws Exception {

        //given
        RequestMatcherInterceptor requestMatcherInterceptor = new RequestMatcherInterceptor(loginInterceptor);
        requestMatcherInterceptor.addIncludingRequestPattern("/**")
                .addExcludingRequestPattern("/test", HttpMethod.GET);

        // when & then
        Assertions.assertThat(requestMatcherInterceptor.preHandle(request, response, handler)).isTrue();
    }

    @Test
    void False() throws Exception {

        //given
        RequestMatcherInterceptor requestMatcherInterceptor = new RequestMatcherInterceptor(loginInterceptor);
        requestMatcherInterceptor.addIncludingRequestPattern("/**");

        // when & then
        Assertions.assertThat(requestMatcherInterceptor.preHandle(request, response, handler)).isFalse();
    }

    @Test
    void test() {
        List<Integer> list = new ArrayList<>();
        Assertions.assertThat(list.stream().noneMatch(value -> value == 1)).isTrue();
        Assertions.assertThat(list.stream().anyMatch(value -> value == 2)).isFalse();
    }

}