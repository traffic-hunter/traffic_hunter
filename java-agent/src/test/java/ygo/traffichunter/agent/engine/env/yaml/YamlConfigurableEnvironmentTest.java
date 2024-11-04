package ygo.traffichunter.agent.engine.env.yaml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ygo.TestExt;
import ygo.traffichunter.agent.property.TrafficHunterAgentProperty;

class YamlConfigurableEnvironmentTest extends TestExt {

    @Test
    void yaml_파일을_제대로_파싱하는지_확인한다() {
        YamlConfigurableEnvironment environment = new YamlConfigurableEnvironment("agent-env.yml");

        TrafficHunterAgentProperty load = environment.load();

        System.out.println(load);
    }

    @ParameterizedTest
    @CsvSource({"max-attempt, maxAttempt", "agent, agent", "aaa, aaa"})
    void 케밥_케이스를_카멜_케이스로_변경(String kebab, String compare) {

        String[] split = kebab.split("-");

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < split.length; i++) {
            if(i == 0) {
                sb.append(split[i]);
                continue;
            }
            sb.append(split[i].replaceFirst("^[a-z]", String.valueOf(split[i].charAt(0)).toUpperCase()));
        }

        Assertions.assertEquals(compare, sb.toString());
    }
}