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
package ygo.traffichunter.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
public class UUIDGenerator {

    private static final Logger log = LoggerFactory.getLogger(UUIDGenerator.class);

    public static String generate(final String agentName) {
        Path path = Paths.get(getPath(agentName));

        if(!Files.exists(path.getParent())) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                log.error("Failed to create directory = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

        if(Files.exists(path)) {
            try {
                return Files.readString(path).trim();
            } catch (IOException e) {
                log.error("Failed to read file = {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }

        final String uuid = UUID.randomUUID().toString();

        try {
            Files.writeString(path, uuid);
            return uuid;
        } catch (IOException e) {
            log.error("Failed to write file = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String getPath(final String agentName) {
        return System.getProperty("user.home")
                + "/traffic-hunter"
                + "/key"
                + "/"
                + agentName
                + "_"
                + "agent_id.txt";
    }
}
