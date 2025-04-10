package org.traffichunter.plugin.spring.autoconfigure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestClient;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@DisplayNameGeneration(ReplaceUnderscores.class)
class SpringWebInstrumentationAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
                    .withBean(RestClient.class, RestClient::create)
                    .withConfiguration(AutoConfigurations.of(SpringWebInstrumentationAutoConfiguration.class));

    @Test
    void rest_client_instrumentation_enabled() {
        runner.run(context -> {
            assertThat(context.getBean("thunterRestClientBeanPostProcessor", RestClientBeanPostProcessor.class))
                    .isNotNull();

            context.getBean(RestClient.class)
                    .mutate()
                    .requestInterceptors(interceptor -> {
                        long count = interceptor.stream()
                                .filter(chri -> chri.getClass()
                                        .getName()
                                        .startsWith("org.traffichunter.plugin")
                                ).count();

                        assertThat(count).isEqualTo(1);
                    });
        });
    }
}