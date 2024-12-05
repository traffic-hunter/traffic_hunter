package ygo.traffichunter.util;

import org.junit.jupiter.api.Test;
import ygo.AbstractTest;

class UUIDGeneratorTest extends AbstractTest {

    @Test
    void UUID_KEY_파일을_생성하고_UUID_KEY를_넣는다() {
        String generate = UUIDGenerator.generate();

        System.out.println(generate);
    }
}