/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffichunter.agent.banner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;
import ygo.traffichunter.agent.engine.metric.metadata.AgentMetadata;

/**
 * banner print.
 * @author yungwang-o
 * @version 1.0.0
 */
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
