/**
 * The MIT License
 *
 * Copyright (c) 2024 traffic-hunter.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.traffichunter.javaagent.plugin.jdbc.library;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import org.traffichunter.javaagent.plugin.jdbc.library.DatabaseInfo.Builder;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public enum JdbcInformationParser {

    MYSQL("mysql", "mariadb") {

        private static final String DEFAULT_PORT = "3306";

        @Override
        public DatabaseInfo doParse(final String jdbcUrl, final Properties properties) {
            Builder builder = JdbcInformationParser.parse(jdbcUrl, properties);

            DatabaseInfo dbInfo = builder.build();

            if(Objects.isNull(dbInfo.getPort())) {
                builder.port(DEFAULT_PORT);
            }
            if(Objects.isNull(dbInfo.getHost())) {
                builder.host(DEFAULT_HOST);
            }

            return dbInfo;
        }
    },
    H2("h2") {

        @Override
        public DatabaseInfo doParse(final String jdbcUrl, final Properties properties) {
            Builder builder = JdbcInformationParser.parse(jdbcUrl, properties);

            return builder.build();
        }
    },
    POSTGRESQL("postgresql") {

        private static final String DEFAULT_PORT = "5432";

        @Override
        public DatabaseInfo doParse(final String jdbcUrl, final Properties properties) {
            Builder builder = JdbcInformationParser.parse(jdbcUrl, properties);

            DatabaseInfo dbInfo = builder.build();

            if(Objects.isNull(dbInfo.getPort())) {
                builder.port(DEFAULT_PORT);
            }
            if(Objects.isNull(dbInfo.getHost())) {
                builder.host(DEFAULT_HOST);
            }

            return dbInfo;
        }
    },
    ORACLE("oracle") {

        private static final String DEFAULT_PORT = "1521";

        @Override
        public DatabaseInfo doParse(final String jdbcUrl, final Properties properties) {
            Builder builder = JdbcInformationParser.parse(jdbcUrl, properties);

            DatabaseInfo dbInfo = builder.build();

            if(Objects.isNull(dbInfo.getPort())) {
                builder.port(DEFAULT_PORT);
            }
            if(Objects.isNull(dbInfo.getHost())) {
                builder.host(DEFAULT_HOST);
            }

            return dbInfo;
        }
    },
    MONGO("mongodb") {

        private static final String DEFAULT_PORT = "27017";

        @Override
        public DatabaseInfo doParse(final String jdbcUrl, final Properties properties) {
            Builder builder = JdbcInformationParser.parse(jdbcUrl, properties);

            DatabaseInfo dbInfo = builder.build();

            if(Objects.isNull(dbInfo.getPort())) {
                builder.port(DEFAULT_PORT);
            }
            if(Objects.isNull(dbInfo.getHost())) {
                builder.host(DEFAULT_HOST);
            }

            return dbInfo;
        }
    },
    ;

    private final List<String> dbNames;

    JdbcInformationParser(final String... dbNames) {
        this.dbNames = Collections.unmodifiableList(Arrays.asList(dbNames));
    }

    abstract DatabaseInfo doParse(final String jdbcUrl, final Properties properties);

    public List<String> getDbNames() {
        return dbNames;
    }

    private static final Map<String, JdbcInformationParser> map = new HashMap<>();

    private static final String DEFAULT_HOST = "localhost";

    static {

        for (JdbcInformationParser db : values()) {
            for (String dbName : db.getDbNames()) {
                map.put(dbName, db);
            }
        }
    }

    public static DatabaseInfo selectDatabase(final String url, final Properties properties) {

        if(url.startsWith("jdbc:")) {

            final String system = parseDbSystem(url);

            JdbcInformationParser jdbcInformationParser = Optional.of(map.get(system))
                    .orElseThrow(() -> new IllegalArgumentException("not supported db system : " + system));

            return jdbcInformationParser.doParse(url, properties);
        } else {
            throw new IllegalArgumentException("Unknown url: " + url);
        }
    }

    private static String parseDbSystem(final String url) {

        String str = url.substring("jdbc:".length());

        int idxOf = str.indexOf(":");

        return str.substring(0, idxOf);
    }

    private static DatabaseInfo.Builder parse(final String url, final Properties properties) {

        if(!Objects.isNull(url) && !properties.isEmpty()) {
            Builder builder = DatabaseInfo.builder();

            builder.url(url);

            if (properties.containsKey("user")) {
                builder.user((String) properties.get("user"));
            }

            if (properties.containsKey("databasename")) {
                builder.db((String) properties.get("databasename"));
            }
            if(properties.containsKey("databaseName")) {
                builder.db((String) properties.get("databaseName"));
            }

            if(properties.containsKey("servername")) {
                builder.host((String) properties.get("servername"));
            }
            if(properties.containsKey("serverName")) {
                builder.host((String) properties.get("serverName"));
            }

            if(properties.containsKey("portnumber")) {
                builder.port((String) properties.get("portnumber"));
            }
            if(properties.containsKey("portNumber")) {
                builder.port((String) properties.get("portNumber"));
            }

            builder.system(parseDbSystem(url));

            return builder;

        } else {
            throw new IllegalArgumentException("properties is empty");
        }
    }
}
