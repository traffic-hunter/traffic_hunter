package ygo.traffic_hunter.core.identification;

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
}
