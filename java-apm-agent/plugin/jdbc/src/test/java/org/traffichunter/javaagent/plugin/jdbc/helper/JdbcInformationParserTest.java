package org.traffichunter.javaagent.plugin.jdbc.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class JdbcInformationParserTest {

    @Test
    void jdbc_url을_파싱한다() {

        String jdbcUrl = "jdbc:mysql://localhost:3306/test?user=admin&password=1234&serverTimezone=UTC&useSSL=false";;

        String regex = "jdbc:(\\w+):\\/\\/([^:/]+)(?::(\\d+))?\\/(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jdbcUrl);

        if(matcher.find()) {
            String dbms = matcher.group(1);
            String host = matcher.group(2);
            String port = matcher.group(3);
            String database = matcher.group(4);

            assertEquals(dbms, "mysql");
            assertEquals(host, "localhost");
            assertEquals(port, "3306");
            assertEquals(database, "test");
        }
    }
}