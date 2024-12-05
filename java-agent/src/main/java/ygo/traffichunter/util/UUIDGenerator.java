package ygo.traffichunter.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDGenerator {

    private static final Logger log = LoggerFactory.getLogger(UUIDGenerator.class);

    public static String generate() {
        Path path = Paths.get(getPath());

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

    private static String getPath() {
        return System.getProperty("user.home")
                + "/traffic-hunter"
                + "/key"
                + "/agent_id.txt";
    }
}
