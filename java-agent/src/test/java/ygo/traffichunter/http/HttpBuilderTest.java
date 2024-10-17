package ygo.traffichunter.http;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import ygo.traffichunter.agent.engine.systeminfo.SystemInfo;
import ygo.traffichunter.agent.engine.systeminfo.cpu.CpuStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.gc.GarbageCollectionStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.memory.MemoryStatusInfo.MemoryUsage;
import ygo.traffichunter.agent.engine.systeminfo.runtime.RuntimeStatusInfo;
import ygo.traffichunter.agent.engine.systeminfo.thread.ThreadStatusInfo;

class HttpBuilderTest {

    @Test
    void 서버로_잘_송신이_되는지_확인한다() throws Exception {
        // given
        SystemInfo systemInfo = new SystemInfo(
                new MemoryStatusInfo(new MemoryUsage(1, 1, 1, 1), new MemoryUsage(1,1, 1, 1)),
                new ThreadStatusInfo(1,1, 1),
                new CpuStatusInfo(1, 1, 1),
                new GarbageCollectionStatusInfo(List.of()),
                new RuntimeStatusInfo(1, 1, "", "")
        );

        // when
        HttpResponse<String> httpResponse = HttpBuilder.newBuilder(URI.create("http://localhost:9100/traffic-hunter"))
                .header("Content-Type", "application/json")
                .timeOut(Duration.ofSeconds(10))
                .request(systemInfo)
                .build();

        // then
        assertEquals(httpResponse.statusCode(), 200);
    }
}