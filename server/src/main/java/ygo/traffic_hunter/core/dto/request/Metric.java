package ygo.traffic_hunter.core.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ygo.traffic_hunter.core.dto.request.systeminfo.SystemInfo;
import ygo.traffic_hunter.core.dto.request.transaction.TransactionInfo;


/**
 * type metric marker interface
 * <br/>
 * {@link TransactionInfo}
 * <br/>
 * {@link SystemInfo}
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SystemInfo.class, name = "system"),
        @JsonSubTypes.Type(value = TransactionInfo.class, name = "transaction")
})
public interface Metric {
}
