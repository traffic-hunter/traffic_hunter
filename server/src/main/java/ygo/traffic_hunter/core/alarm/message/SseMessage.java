package ygo.traffic_hunter.core.alarm.message;

import java.util.List;
import ygo.traffic_hunter.core.alarm.message.Message.Embed;

public record SseMessage(
        String title,
        int color,
        String username,
        List<Field> fields
) {

    public static SseMessage from(final Message message) {
        Embed embed = message.getEmbeds().getFirst();
        return new Builder()
                .title(embed.getTitle())
                .color(embed.getColor())
                .username(message.getUsername())
                .fields(convertFields(embed.getFields()))
                .build();
    }

    private static List<Field> convertFields(List<Message.Field> fields) {
        return fields.stream()
                .map(field -> new Field(field.getName(), field.getValue()))
                .toList();

    }

    public record Field(
            String title,
            String value
    ) {
    }

    public static class Builder {
        private String title;
        private int color;
        private String username;
        private List<Field> fields;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder fields(List<Field> fields) {
            this.fields = fields;
            return this;
        }

        public SseMessage build() {
            return new SseMessage(title, color, username, fields);
        }
    }

}


