package ygo.traffichunter.agent.banner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import ygo.traffichunter.agent.engine.systeminfo.metadata.AgentMetadata;

public class AsciiBanner {

    private static final String BANNER_NAME = "agent-banner.txt";

    public void print(final AgentMetadata metadata) {
        try (final InputStream in = AsciiBanner.class.getClassLoader().getResourceAsStream(BANNER_NAME)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)));

            String banner = reader.lines()
                    .map(line -> line
                            .replace("${version}", metadata.agentVersion())
                            .replace("${java.version}", System.getProperty("java.version"))
                            .replace("${java.specification}", System.getProperty("java.specification.version"))
                            .replace("${jdk}", System.getProperty("java.vendor"))
                            .replace("${time}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                    .collect(Collectors.joining(System.lineSeparator()));

            System.out.println(banner + "\n");

        } catch (IOException ignored) {

        }
    }
}
