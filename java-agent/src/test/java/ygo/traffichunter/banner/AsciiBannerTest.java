package ygo.traffichunter.banner;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.agent.banner.AsciiBanner;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;

class AsciiBannerTest extends AbstractTest {

    @Test
    void 배너_프린트를_테스트한다() {
        AsciiBanner asciiBanner = new AsciiBanner();

        asciiBanner.print(new AgentMetadata("1", "1", "1", Instant.now(), new AtomicReference<>()));
    }
}