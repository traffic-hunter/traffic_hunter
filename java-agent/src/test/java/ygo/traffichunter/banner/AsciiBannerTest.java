package ygo.traffichunter.banner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import ygo.TestExt;

class AsciiBannerTest extends TestExt {

    @Test
    void 배너_프린트를_테스트한다() {
        AsciiBanner.print();
    }
}