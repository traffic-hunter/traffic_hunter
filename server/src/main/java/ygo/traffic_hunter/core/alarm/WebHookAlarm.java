package ygo.traffic_hunter.core.alarm;

import ygo.traffic_hunter.core.webhook.Webhook;

public interface WebHookAlarm extends Alarm {

    Webhook getWebhook();

    void enable();

    void disable();
}
