package ygo.traffichunter.util;

import java.io.FileInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;

class FileUtilsTest extends AbstractTest {

    @Test
    void 파일_경로를_잘_읽어오는지_확인한다() {

        FileInputStream file = FileUtils.getFile("/Users/yungwang-o/Documents/agent-env.yml");

        Assertions.assertNotNull(file);
    }
}