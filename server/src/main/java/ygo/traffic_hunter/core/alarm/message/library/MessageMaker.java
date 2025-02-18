/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ygo.traffic_hunter.core.alarm.message.library;

import java.net.InetAddress;
import java.time.Instant;
import lombok.Getter;
import org.slf4j.helpers.MessageFormatter;
import ygo.traffic_hunter.core.alarm.message.Message;
import ygo.traffic_hunter.core.alarm.message.Message.Embed;
import ygo.traffic_hunter.core.alarm.message.Message.Field;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
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

    @Deprecated(since = "1.1.0")
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
                                .color(color.getValue())
                                .addField(Field.of("title", title, false))
                                .addField(Field.of("message", bodyMessage, false))
                                .build()
                ).build();
    }

    private record Body(String message, Object[] args) { }

    @Getter
    public enum Color {
        RED(0xFF0000, "FF0000"),
        BLUE(0x0000FF, "0000FF"),
        GREEN(0x00FF00, "00FF00"),
        ;

        private final int value;

        private final String stringValue;

        Color(final int value, final String stringValue) {
            this.value = value;
            this.stringValue = stringValue;
        }
    }
}
