package ygo.traffic_hunter.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ygo.traffic_hunter.AbstractTestConfiguration;
import ygo.traffic_hunter.core.assembler.Assembler;
import ygo.traffic_hunter.core.assembler.span.SpanAssembler;
import ygo.traffic_hunter.core.assembler.span.SpanTreeNode;
import ygo.traffic_hunter.core.dto.response.RealTimeMonitoringResponse;
import ygo.traffic_hunter.core.dto.response.SystemMetricResponse;
import ygo.traffic_hunter.core.dto.response.TransactionMetricResponse;
import ygo.traffic_hunter.core.service.MetricService;
import ygo.traffic_hunter.domain.interval.TimeInterval;
import ygo.traffic_hunter.domain.metric.MetricData;
import ygo.traffic_hunter.domain.metric.TransactionData;
import ygo.traffic_hunter.domain.metric.cpu.CpuMetricMeasurement;
import ygo.traffic_hunter.domain.metric.dbcp.hikari.HikariCPMeasurement;
import ygo.traffic_hunter.domain.metric.gc.GCMetricMeasurement;
import ygo.traffic_hunter.domain.metric.gc.time.GCMetricCollectionTime;
import ygo.traffic_hunter.domain.metric.memory.MemoryMetricMeasurement;
import ygo.traffic_hunter.domain.metric.memory.usage.MemoryMetricUsage;
import ygo.traffic_hunter.domain.metric.runtime.RuntimeMetricMeasurement;
import ygo.traffic_hunter.domain.metric.thread.ThreadMetricMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.TomcatWebServerMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.request.TomcatWebServerRequestMeasurement;
import ygo.traffic_hunter.domain.metric.web.tomcat.thread.TomcatWebServerThreadPoolMeasurement;
import ygo.traffic_hunter.presentation.advice.MetricControllerAdvice;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ServerSentEventController.class)
@EnableAspectJAutoProxy
class ServerSentEventControllerTest extends AbstractTestConfiguration {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private MetricService metricService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ServerSentEventController(metricService))
                .apply(documentationConfiguration(restDocumentation))
                .setControllerAdvice(new MetricControllerAdvice())
                .build();
    }


    @Test
    void 구독API_요청과_응답에_대한_테스트를_진행한다_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/metrics/subscribe")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andDo(document("metrics-subscribe"));
    }

    @Test
    void BROADCAST_API_요청과_응답에_대한_테스트를_진행한다_200() throws Exception {

        RealTimeMonitoringResponse realTimeMonitoringResponse = new RealTimeMonitoringResponse(
                List.of(getSystemMetricResponse()), List.of(getTransactionMetricResponse()));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/metrics/broadcast/{interval}", TimeInterval.REAL_TIME)
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andDo(result -> {
                    MockHttpServletResponse response = result.getResponse();
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter()
                            .write(objectMapper.writerWithDefaultPrettyPrinter()
                                    .writeValueAsString(realTimeMonitoringResponse));
                })
                .andDo(document("metrics-broadcast"
                        , queryParameters(
                                parameterWithName("limit").optional().description("조회할 개수(기본값 20)")
                        )
                        , pathParameters(
                                parameterWithName("interval")
                                        .description(Arrays.stream(TimeInterval.values()).map(Enum::name).collect(
                                                Collectors.joining(", ")))
                        )
                        , responseFields(
                                fieldWithPath("systemMetricResponses").description("시스템 메트릭 응답 목록"),
                                fieldWithPath("systemMetricResponses[].time").description("응답 수집 시간 (ISO 8601 형식)"),
                                fieldWithPath("systemMetricResponses[].agentName").description("에이전트 이름"),
                                fieldWithPath("systemMetricResponses[].agentBootTime").description(
                                        "에이전트 시작 시간"),
                                fieldWithPath("systemMetricResponses[].agentVersion").description("에이전트 버전"),
                                fieldWithPath("systemMetricResponses[].metricData").description("시스템 메트릭 데이터"),

                                // CPU 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.cpuMetric").description(
                                        "CPU 메트릭 데이터"),
                                fieldWithPath("systemMetricResponses[].metricData.cpuMetric.systemCpuLoad").description(
                                        "시스템 CPU 사용량"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.cpuMetric.processCpuLoad").description(
                                        "프로세스 CPU 사용량"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.cpuMetric.availableProcessors").description(
                                        "사용 가능한 프로세서 수"),

                                // GC 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.gcMetric").description("GC 메트릭 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.gcMetric.gcMetricCollectionTimes").description(
                                        "GC 수집 시간 정보 목록"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.gcMetric.gcMetricCollectionTimes[].getCollectionCount").description(
                                        "GC 호출 횟수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.gcMetric.gcMetricCollectionTimes[].getCollectionTime").description(
                                        "GC 총 수집 시간(ms)"),

                                // 메모리 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.memoryMetric").description(
                                        "메모리 메트릭 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.heapMemoryUsage").description(
                                        "힙 메모리 사용량"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.heapMemoryUsage.init").description(
                                        "힙 메모리 초기 크기"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.heapMemoryUsage.used").description(
                                        "힙 메모리 사용량"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.heapMemoryUsage.committed").description(
                                        "힙 메모리 커밋 크기"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.heapMemoryUsage.max").description(
                                        "힙 메모리 최대 크기"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.nonHeapMemoryUsage").description(
                                        "논힙 메모리 사용량"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.nonHeapMemoryUsage.init").description(
                                        "논힙 메모리 초기 크기"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.nonHeapMemoryUsage.used").description(
                                        "논힙 메모리 사용량"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.nonHeapMemoryUsage.committed").description(
                                        "논힙 메모리 커밋 크기"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.memoryMetric.nonHeapMemoryUsage.max").description(
                                        "논힙 메모리 최대 크기"),

                                // 런타임 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.runtimeMetric").description(
                                        "런타임 메트릭 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.runtimeMetric.getStartTime").description(
                                        "시작 시간 (타임스탬프)"),
                                fieldWithPath("systemMetricResponses[].metricData.runtimeMetric.getUpTime").description(
                                        "업타임(ms)"),
                                fieldWithPath("systemMetricResponses[].metricData.runtimeMetric.getVmName").description(
                                        "VM 이름"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.runtimeMetric.getVmVersion").description(
                                        "VM 버전"),

                                // 스레드 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.threadMetric").description(
                                        "스레드 메트릭 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.threadMetric.threadCount").description(
                                        "현재 스레드 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.threadMetric.getPeekThreadCount").description(
                                        "최대 스레드 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.threadMetric.getTotalStartThreadCount").description(
                                        "시작된 총 스레드 수"),

                                // 웹 서버 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.webServerMetric").description(
                                        "웹 서버 메트릭 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerRequestMeasurement").description(
                                        "Tomcat 요청 관련 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerRequestMeasurement.requestCount").description(
                                        "총 요청 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerRequestMeasurement.bytesReceived").description(
                                        "수신된 바이트 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerRequestMeasurement.bytesSent").description(
                                        "전송된 바이트 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerRequestMeasurement.processingTime").description(
                                        "처리 시간(ms)"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerRequestMeasurement.errorCount").description(
                                        "에러 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerThreadPoolMeasurement").description(
                                        "Tomcat 스레드 풀 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerThreadPoolMeasurement.maxThreads").description(
                                        "최대 스레드 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerThreadPoolMeasurement.currentThreads").description(
                                        "현재 스레드 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.webServerMetric.tomcatWebServerThreadPoolMeasurement.currentThreadsBusy").description(
                                        "활성 스레드 수"),

                                // DBCP 메트릭
                                fieldWithPath("systemMetricResponses[].metricData.dbcpMetric").description(
                                        "DBCP 메트릭 데이터"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.dbcpMetric.activeConnections").description(
                                        "활성 연결 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.dbcpMetric.idleConnections").description(
                                        "유휴 연결 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.dbcpMetric.totalConnections").description(
                                        "총 연결 수"),
                                fieldWithPath(
                                        "systemMetricResponses[].metricData.dbcpMetric.threadsAwaitingConnection").description(
                                        "연결 대기 중인 스레드 수"),

                                fieldWithPath("transactionMetricResponses[].agentName").description("에이전트의 이름"),
                                fieldWithPath("transactionMetricResponses[].agentBootTime").description("에이전트 부팅 시간"),
                                fieldWithPath("transactionMetricResponses[].agentVersion").description("에이전트의 버전"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.name").description(
                                        "트랜잭션이 수행된 메서드의 이름과 경로"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.traceId").description(
                                        "트랜잭션의 고유 식별자"),
                                fieldWithPath(
                                        "transactionMetricResponses[].spanTreeNode.data.parentSpanId").description(
                                        "부모 메서드의 ID (루트 Span의 경우 0000000000000000)"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.spanId").description(
                                        "현재 메서드의 ID"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.attributes").description(
                                        "메서드의 세부 정보 ex) { http.method : GET }"),
                                fieldWithPath(
                                        "transactionMetricResponses[].spanTreeNode.data.attributesCount").description(
                                        "세부 정보 개수"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.startTime").description(
                                        "메서드 시작 시간 (ISO 8601 형식)"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.endTime").description(
                                        "메서드 종료 시간 (ISO 8601 형식)"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.duration").description(
                                        "메서드 실행 시간 (ms 단위)"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.exception").description(
                                        "메서드 수행 결과 (예: success 또는 예외 메시지)"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.data.ended").description(
                                        "메서드 종료 여부 (true: 종료됨)"),
                                fieldWithPath("transactionMetricResponses[].spanTreeNode.children[]").description(
                                        "현재 메서드 자식 목록")
                        )
                ));
    }

    private SystemMetricResponse getSystemMetricResponse() {
        Instant time = getInstant("2025-01-21 18:20:27.822166");
        String agentName = "myAgent";
        Instant agentBootTime = getInstant("2025-01-21 18:20:18.933976");
        String agentVersion = "1.0.0";
        MetricData metricData = getMetricData();
        return new SystemMetricResponse(time, agentName, agentBootTime, agentVersion, metricData);
    }

    private MetricData getMetricData() {
        CpuMetricMeasurement cpuMetric = new CpuMetricMeasurement(0.0, 0.14010085468246197, 12);
        GCMetricMeasurement gcMetric = new GCMetricMeasurement(
                List.of(new GCMetricCollectionTime(61, 16), new GCMetricCollectionTime(17, 6)));
        MemoryMetricMeasurement memoryMetric = new MemoryMetricMeasurement(
                new MemoryMetricUsage(536870912L, 78528144L, 113246208L, 8577351680L),
                new MemoryMetricUsage(2555904L, 103137872L, 105119744L, -1));
        RuntimeMetricMeasurement runtimeMetric = new RuntimeMetricMeasurement(173748361863L, 9652,
                "OpenJDK 64-Bit Server VM", "21.0.5+11-LTS");
        ThreadMetricMeasurement threadMetric = new ThreadMetricMeasurement(35, 35, 40);
        TomcatWebServerMeasurement webServerMetric = new TomcatWebServerMeasurement(
                new TomcatWebServerRequestMeasurement(0, 0, 0, 0, 0),
                new TomcatWebServerThreadPoolMeasurement(200, 10, 0));
        HikariCPMeasurement dbcpMetric = new HikariCPMeasurement(10, 10, 0, 0);
        return new MetricData(cpuMetric, gcMetric, memoryMetric, runtimeMetric, threadMetric, webServerMetric,
                dbcpMetric);
    }

    private Instant getInstant(String dateStr) {
        // 날짜 문자열을 LocalDateTime으로 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);

        // LocalDateTime을 UTC 시간대의 Instant로 변환
        return localDateTime.atZone(ZoneOffset.UTC).toInstant();
    }

    private TransactionMetricResponse getTransactionMetricResponse() {
        Instant agentBootTime = getInstant("2025-01-21 18:20:18.933976");
        String agentVersion = "1.0.0";
        return new TransactionMetricResponse("myAgent", agentBootTime, agentVersion, getSpanTree());
    }

    private SpanTreeNode getSpanTree() {
        List<TransactionData> datas = new ArrayList<>();

        datas.add(createTransactionData("0000000000000000", "a"));
        Assembler<List<TransactionData>, SpanTreeNode> assembler = new SpanAssembler();

        return assembler.assemble(datas);
    }

    private TransactionData createTransactionData(final String parentSpanId, final String spanId) {
        return TransactionData.builder()
                .parentSpanId(parentSpanId)
                .spanId(spanId)
                .traceId("traceId")
                .ended(true)
                .name("test")
                .attributesCount(2)
                .attributes(Map.of())
                .endTime(Instant.now())
                .startTime(Instant.now())
                .exception("exception")
                .duration(30)
                .build();
    }

    @Test
    void BROADCAST_API_요청시_구독을_하지_않았다면_예외가_발생한다_400() throws Exception {

        // given
        willThrow(new IllegalStateException(
                "The client with the given identification does not exist. Please subscribe first."))
                .given(metricService)
                .scheduleBroadcast(any(), any(), anyInt());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/metrics/broadcast/{interval}", TimeInterval.REAL_TIME)
                        .param("limit", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("400-error-metrics-broadcast"
                        , preprocessRequest(prettyPrint())
                        , preprocessResponse(prettyPrint())
                ));
    }

}