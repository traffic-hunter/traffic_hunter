package ygo.traffic_hunter.presentation.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.dto.request.statistics.StatisticsRequest;
import ygo.traffic_hunter.core.dto.response.statistics.transaction.ServiceTransactionResponse;
import ygo.traffic_hunter.core.service.MetricStatisticsService;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = MetricController.class)
class MetricControllerTest extends AbstractTestConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    MetricStatisticsService metricStatisticsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MetricController(metricStatisticsService))
                .apply(documentationConfiguration(restDocumentation))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void 서비스_트랜잭션은_정상적으로_동작한다_200() throws Exception {
        // given
        StatisticsRequest sr = new StatisticsRequest(
                Instant.now().minus(Duration.ofDays(1)),
                Instant.now()
        );

        ServiceTransactionResponse str = new ServiceTransactionResponse(
                "/test",
                0,
                0,
                0.0,
                0,
                0
        );

        // when
        ResultActions resultActions = mockMvc.perform(get("/statistics/transaction")
                .content(objectMapper.writeValueAsString(sr))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "count,DESC"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter()
                            .write(objectMapper.writeValueAsString(str));
                })
                .andDo(print())
                .andDo(document("service-transaction"
                        , queryParameters(
                                parameterWithName("page").description("페이지 넘버 (default 0)"),
                                parameterWithName("size").description("페이지 사이즈 (default 10)"),
                                parameterWithName("sort").description("페이지 정렬 (default count,DESC)")
                        )
                        , requestFields(
                                fieldWithPath("begin").description("조회 시작 시간 (ISO 8601 형식)"),
                                fieldWithPath("end").description("조회 종료 시간 (ISO 8601 형식, 선택적)")
                        )
                        , responseFields(
                                fieldWithPath("url").description("해당 서비스의 url"),
                                fieldWithPath("count").description("해당 서비스의 요청 개수"),
                                fieldWithPath("errCount").description("해당 서비스의 에러 개수"),
                                fieldWithPath("avgExecutionTime").description("해당 서비스의 평균 실행 시간"),
                                fieldWithPath("sumExecutionTime").description("해당 서비스의 총 실행 시간"),
                                fieldWithPath("maxExecutionTime").description("해당 서비스의 가장 높은 실행 시간")
                        )
                ));
    }
}