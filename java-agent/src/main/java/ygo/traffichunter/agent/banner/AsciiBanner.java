package ygo.traffichunter.agent.banner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

public class AsciiBanner {

    private static final String BANNER_NAME = "banner.txt";

    private static final String VERSION = "1.0.0";

    public void print() {
        try (final InputStream in = getClass().getClassLoader().getResourceAsStream(BANNER_NAME)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(in)));

            String banner = reader.lines()
                    .map(line -> line
                            .replace("${version}", VERSION)
                            .replace("${java.version}", System.getProperty("java.version"))
                            .replace("${java.specification}", System.getProperty("java.specification.version"))
                            .replace("${jdk}", System.getProperty("java.vendor"))
                            .replace("${time}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                    .collect(Collectors.joining(System.lineSeparator()));

            System.out.println(banner);

        } catch (IOException ignored) {

        }
    }
}
