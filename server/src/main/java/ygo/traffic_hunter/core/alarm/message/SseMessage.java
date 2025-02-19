package ygo.traffic_hunter.core.alarm.message;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import ygo.traffic_hunter.core.alarm.message.Message.Embed;

@Builder
public record SseMessage(
        Instant timestamp,
        String title,
        int color,
        String username,
        List<Field> fields
) {

    public static SseMessage from(final Message message) {

        Embed embed = message.getEmbeds().getFirst();

        return SseMessage.builder()
                .timestamp(message.getTimestamp())
                .title(embed.getTitle())
                .color(embed.getColor())
                .username(message.getUsername())
                .fields(convertFields(embed.getFields()))
                .build();
    }

    private static List<Field> convertFields(final List<Message.Field> fields) {
        return fields.stream()
                .map(Field::from)
                .toList();

    }

    public record Field(String title, String value) {

        public static Field from(final Message.Field field) {
            return new Field(field.getName(), field.getValue());
        }
    }
}


