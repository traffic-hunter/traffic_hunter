package ygo.traffic_hunter.presentation.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.common.web.resolver.MemberArgumentResolver;
import ygo.traffic_hunter.core.dto.request.alarm.ThresholdRequest;
import ygo.traffic_hunter.core.dto.response.alarm.ThresholdResponse;
import ygo.traffic_hunter.core.service.AlarmService;
import ygo.traffic_hunter.presentation.advice.GlobalControllerAdvice;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = AlarmController.class)
class AlarmControllerTest extends AbstractTestConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    AlarmService alarmService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AlarmController(alarmService))
                .apply(documentationConfiguration(restDocumentation))
                .setCustomArgumentResolvers(new MemberArgumentResolver())
                .setControllerAdvice(new GlobalControllerAdvice())
                .build();
    }

    @Test
    void threshold를_조회한다_200() throws Exception {
        // given
        ThresholdResponse thresholdResponse = new ThresholdResponse(
                80,
                80
                ,80
                ,80
                ,80
                ,80
        );

        given(alarmService.retrieveThreshold()).willReturn(thresholdResponse);

        // when
        ResultActions resultActions = mockMvc.perform(get("/alarms/threshold"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("alarm/get-threshold"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , responseFields(
                                fieldWithPath("cpuThreshold").description("cpu 임계치"),
                                fieldWithPath("memoryThreshold").description("memory 임계치"),
                                fieldWithPath("threadThreshold").description("thread 임계치"),
                                fieldWithPath("webRequestThreshold").description("web request 임계치"),
                                fieldWithPath("webThreadThreshold").description("web thread 임계치"),
                                fieldWithPath("dbcpThreshold").description("dbcp 임계치")
                        )
                        ));
    }

    @Test
    void threshold를_업데이트한다_204() throws Exception {
        // given
        ThresholdRequest thresholdRequest = new ThresholdRequest(
                80,
                80
                ,80
                ,80
                ,80
                ,80
        );

        willDoNothing()
                .given(alarmService)
                .updateThreshold(
                        thresholdRequest.cpuThreshold(),
                        thresholdRequest.memoryThreshold(),
                        thresholdRequest.threadThreshold(),
                        thresholdRequest.webRequestThreshold(),
                        thresholdRequest.webThreadThreshold(),
                        thresholdRequest.dbcpThreshold()
                );

        // when
        ResultActions resultActions = mockMvc.perform(put("/alarms/threshold")
                .content(objectMapper.writeValueAsString(thresholdRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("alarm/update-threshold"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                        , requestFields(
                                fieldWithPath("cpuThreshold").description("cpu 임계치"),
                                fieldWithPath("memoryThreshold").description("memory 임계치"),
                                fieldWithPath("threadThreshold").description("thread 임계치"),
                                fieldWithPath("webRequestThreshold").description("web request 임계치"),
                                fieldWithPath("webThreadThreshold").description("web thread 임계치"),
                                fieldWithPath("dbcpThreshold").description("dbcp 임계치")
                        )
                ));
    }

    @Test
    @SuppressWarnings("all")
    void threshold_업데이트_요청의_유효성_검사에_실패한다_400() throws Exception {
        // given
        ThresholdRequest thresholdRequest = new ThresholdRequest(
                150,
                80
                ,80
                ,80
                ,80
                ,80
        );

        willDoNothing()
                .given(alarmService)
                .updateThreshold(thresholdRequest.cpuThreshold(),
                        thresholdRequest.memoryThreshold(),
                        thresholdRequest.threadThreshold(),
                        thresholdRequest.webRequestThreshold(),
                        thresholdRequest.webThreadThreshold(),
                        thresholdRequest.dbcpThreshold()
                );

        // when
        ResultActions resultActions = mockMvc.perform(put("/alarms/threshold")
                .content(objectMapper.writeValueAsString(thresholdRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("alarm/failed-update-threshold"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                ));
    }
}