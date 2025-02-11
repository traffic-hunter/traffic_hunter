package ygo.traffic_hunter.core.webhook.message.library;

import static ygo.traffic_hunter.core.webhook.message.Message.Embed;
import static ygo.traffic_hunter.core.webhook.message.Message.Field;

import java.net.InetAddress;
import java.time.Instant;
import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;
import ygo.traffic_hunter.core.webhook.message.Message;

public class MessageMaker {

    private Body body;

    private Instant time;

    private String content;

    private String title;

    private String agentName;

    private InetAddress inetAddress;

    private String url;

    private Color color;

    private MessageMaker() {
    }

    public static MessageMaker builder() {
        return new MessageMaker();
    }

    public MessageMaker url(final String url) {
        this.url = url;
        return this;
    }

    public MessageMaker times(final Instant time) {
        this.time = time;
        return this;
    }

    public MessageMaker content(final String content) {
        this.content = content;
        return this;
    }

    public MessageMaker title(final String title) {
        this.title = title;
        return this;
    }

    public MessageMaker agentName(final String agentName) {
        this.agentName = agentName;
        return this;
    }

    public MessageMaker inet(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }

    public MessageMaker color(final Color color) {
        this.color = color;
        return this;
    }

    public MessageMaker description(final String message, final Object... arg) {
        this.body = new Body(message, arg);
        return this;
    }

    public Message build() {

        String bodyMessage = MessageFormatter.arrayFormat(body.message(), body.args())
                .getMessage();

        return Message.builder()
                .time(time)
                .url(url)
                .username(agentName)
                .content(content)
                .addEmbed(
                        Embed.builder()
                                .title(title)
                                .color(color.getValue())
                                .description(bodyMessage)
                                .addField(Field.of("ip", inetAddress.getHostAddress(), true))
                                .build()
                ).build();
    }

    private record Body(String message, Object[] args) { }

    @Getter
    public enum Color {
        RED(0xFF0000),
        BLUE(0x0000FF),
        GREEN(0x00FF00),
        ;

        private final int value;

        Color(final int value) {
            this.value = value;
        }
    }
}
