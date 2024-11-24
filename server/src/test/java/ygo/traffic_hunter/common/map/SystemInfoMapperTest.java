package ygo.traffic_hunter.common.map;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;

@SpringBootTest(classes = SystemInfoMapperTest.class)
public class SystemInfoMapperTest extends AbstractTestConfiguration {

    @Qualifier("systemInfoMapperImpl")
    @Autowired
    private SystemInfoMapper mapper;

    @Test
    void test() {

    }
}