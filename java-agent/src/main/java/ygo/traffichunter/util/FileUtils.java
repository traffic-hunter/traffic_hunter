package ygo.traffichunter.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static FileInputStream getFile(final String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            log.error("Could not open file '{}'", path, e);
            throw new RuntimeException(e);
        }
    }
}
