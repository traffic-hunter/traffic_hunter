package ygo.traffic_hunter.persistence.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class QuerySupport {

    public static String orderByClause(final Pageable pageable) {

        StringBuilder sb = new StringBuilder();

        pageable.getSort().stream()
                .map(QuerySupport::orderBy)
                .forEach(sortName -> sb.append(sortName).append(" "));

        return sb.toString();
    }

    private static String orderBy(final Sort.Order order) {
        return "order by" + " " + order.getProperty() + " " + order.getDirection().name();
    }
}
