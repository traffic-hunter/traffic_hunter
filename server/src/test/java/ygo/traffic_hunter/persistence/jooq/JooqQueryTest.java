package ygo.traffic_hunter.persistence.jooq;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ygo.traffic_hunter.AbstractTestConfiguration;

@SpringBootTest
class JooqQueryTest extends AbstractTestConfiguration {

    @Autowired
    private DSLContext dsl;

    @Test
    void jooq_query_test() {

    }
}
