package ygo.traffic_hunter.core.alarm.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import ygo.traffic_hunter.core.alarm.message.Message.Embed;

import java.time.Instant;
import java.util.List;

@Builder
public record SseMessage(
        Instant timestamp,
        String title,
        int color,
        String username,
        List<Field> fields,
        @JsonIgnore Message message
) {

    public static SseMessage from(final Message message) {

        Embed embed = message.embeds().getFirst();

        return SseMessage.builder()
                .timestamp(message.timestamp())
                .title(embed.title())
                .color(embed.color())
                .username(message.username())
                .fields(convertFields(embed.fields()))
                .message(message)
                .build();
    }

    private static List<Field> convertFields(final List<Message.Field> fields) {
        return fields.stream()
                .map(Field::from)
                .toList();

    }

    public record Field(String title, String value) {

        public static Field from(final Message.Field field) {
            return new Field(field.name(), field.value());
        }
    }
}


