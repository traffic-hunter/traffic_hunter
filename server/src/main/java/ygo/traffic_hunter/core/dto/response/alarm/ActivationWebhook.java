package ygo.traffic_hunter.core.dto.response.alarm;

import ygo.traffic_hunter.core.webhook.Webhook;

public record ActivationWebhook(Webhook webhook, boolean isActive) {
}
