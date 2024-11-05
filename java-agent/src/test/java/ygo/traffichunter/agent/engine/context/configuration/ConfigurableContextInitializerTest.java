package ygo.traffichunter.agent.engine.context.configuration;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ygo.AbstractTest;
import ygo.traffichunter.agent.engine.env.yaml.YamlConfigurableEnvironment;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

class ConfigurableContextInitializerTest extends AbstractTest {

    @Test
    void 설정_파일을_초기화한다() {
        // given
        ConfigurableContextInitializer initializer =
                new ConfigurableContextInitializer(new YamlConfigurableEnvironment("/Users/yungwang-o/Documents/agent-env.yml"));

        // when
        TrafficHunterAgentProperty property = initializer.property();

        // then
        Assertions.assertNotNull(property);
        Assertions.assertEquals(property.targetJVMPath(), "ygo.testapp.TestAppApplication");

        System.out.println(property);
    }

    @Test
    void jar_파일의_절대_경로를_반환한다() {
        String jar = "build/libs/java-agent-0.0.1-SNAPSHOT-all.jar";

        System.out.println(Paths.get(jar).toAbsolutePath());
    }

    @Test
    void 프로퍼티를_타겟_JVM에_attach한다() {
        // given
        ConfigurableContextInitializer initializer =
                new ConfigurableContextInitializer(new YamlConfigurableEnvironment("/Users/yungwang-o/Documents/agent-env.yml"));

        // when
        TrafficHunterAgentProperty property = initializer.property();

        // then
        initializer.attach(property);
    }
}