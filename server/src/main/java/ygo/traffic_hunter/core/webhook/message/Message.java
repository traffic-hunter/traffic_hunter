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
package ygo.traffic_hunter.core.webhook.message;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@Getter
public class Message {

    private final Instant timestamp;

    private final String url;

    private final String username;

    private final String content;

    private final List<Embed> embeds;

    private Message(Instant timestamp, String url, String username, String content, List<Embed> embeds) {
        this.url = url;
        this.username = username;
        this.content = content;
        this.embeds = embeds;
        this.timestamp = timestamp;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Instant timestamp;

        private String url;

        private String username;

        private String content;

        private final List<Embed> embeds = new ArrayList<>();

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder time(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder addEmbed(Embed embed) {
            this.embeds.add(embed);
            return this;
        }

        public Message build() {
            return new Message(timestamp, url, username, content, embeds);
        }
    }

    // Embed 클래스
    @Getter
    public static class Embed {

        private final String title;

        private final String description;

        private final int color;

        private final List<Field> fields;

        private Embed(String title, String description, List<Field> fields, int color) {
            this.title = title;
            this.description = description;
            this.fields = fields;
            this.color = color;
        }

        // Fluent Builder for Embed
        public static EmbedBuilder builder() {
            return new EmbedBuilder();
        }

        public static class EmbedBuilder {

            private String title;

            private String description;

            private int color;

            private final List<Field> fields = new ArrayList<>();

            public EmbedBuilder title(String title) {
                this.title = title;
                return this;
            }

            public EmbedBuilder color(int color) {
                this.color = color;
                return this;
            }

            public EmbedBuilder description(String description) {
                this.description = description;
                return this;
            }

            public EmbedBuilder addField(Field field) {
                this.fields.add(field);
                return this;
            }

            public Embed build() {
                return new Embed(title, description, fields, color);
            }
        }
    }

    @Getter
    public static class Field {

        private final String name;

        private final String value;

        private final boolean inline;

        private Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public static Field of(String name, String value, boolean inline) {
            return new Field(name, value, inline);
        }
    }
}
