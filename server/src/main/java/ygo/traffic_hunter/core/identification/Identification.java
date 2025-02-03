package ygo.traffic_hunter.core.identification;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Identification {

    private final String id;

    protected Identification() {
        this.id = UUID.randomUUID().toString();
    }

    protected Identification(final String id) {
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Identification that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
