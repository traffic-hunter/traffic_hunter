package ygo.traffic_hunter.persistence.query;

import static org.jooq.impl.DSL.name;

import java.util.List;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Pageable;

public class QuerySupport {

    public static List<SortField<Object>> orderByClause(final Pageable pageable) {

        return pageable.getSort().stream()
                .map(order -> {
                    Field<Object> field = DSL.field(name(order.getProperty()));

                    return order.isAscending() ? field.asc() : field.desc();
                }).toList();
    }
}
