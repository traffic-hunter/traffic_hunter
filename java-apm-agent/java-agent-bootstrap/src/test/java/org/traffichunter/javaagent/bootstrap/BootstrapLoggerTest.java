package org.traffichunter.javaagent.bootstrap;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class BootstrapLoggerTest {

    @Test
    void bootstrap_logger_print() {

        BootstrapLogger log = BootstrapLogger.getLogger(BootstrapLogger.class);

        log.info("info {}");
    }
}