package ygo.traffic_hunter.core.webhook.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("webhook")
public record WebHookProperties(String discordUrl, String slackUrl) {
}
