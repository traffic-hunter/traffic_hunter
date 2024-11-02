package ygo.traffichunter.banner;

import org.junit.jupiter.api.Test;
import ygo.TestExt;
import ygo.traffichunter.agent.banner.AsciiBanner;

class AsciiBannerTest extends TestExt {

    @Test
    void 배너_프린트를_테스트한다() {
        AsciiBanner asciiBanner = new AsciiBanner();
        asciiBanner.print();
    }
}