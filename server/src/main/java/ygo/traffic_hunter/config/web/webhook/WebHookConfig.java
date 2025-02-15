package ygo.traffic_hunter.config.web.webhook;

import com.slack.api.Slack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebHookConfig {

    @Bean
    public Slack slack() {
        return Slack.getInstance();
    }
}
