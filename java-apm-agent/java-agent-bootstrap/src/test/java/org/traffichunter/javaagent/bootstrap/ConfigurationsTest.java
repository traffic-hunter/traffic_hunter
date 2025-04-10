package org.traffichunter.javaagent.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.traffichunter.javaagent.bootstrap.Configurations.ConfigProperty;

class ConfigurationsTest {

    @Test
    void banner_mode_no_property() {

        boolean banner = Configurations.banner(ConfigProperty.BANNER_MODE);

        assertFalse(banner);
    }
}