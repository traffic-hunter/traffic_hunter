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

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class DatabaseInfo {

    private static final DatabaseInfo DEFAULT = DatabaseInfo.builder().build();

    private final String system;

    private final String url;

    private final String user;

    private final String db;

    private final String host;

    private final String port;

    private DatabaseInfo(final String system,
                         final String url,
                         final String user,
                         final String db,
                         final String host,
                         final String port) {

        this.system = system;
        this.url = url;
        this.user = user;
        this.db = db;
        this.host = host;
        this.port = port;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public interface Builder {

        Builder system(String system);

        Builder url(String url);

        Builder user(String user);

        Builder db(String db);

        Builder host(String host);

        Builder port(String port);

        DatabaseInfo build();
    }

    private static class BuilderImpl implements Builder {

        private String system;

        private String url;

        private String user;

        private String db;

        private String host;

        private String port;

        @Override
        public Builder system(final String system) {
            this.system = system;
            return this;
        }

        @Override
        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        @Override
        public Builder user(final String user) {
            this.user = user;
            return this;
        }

        @Override
        public Builder db(final String db) {
            this.db = db;
            return this;
        }

        @Override
        public Builder host(final String host) {
            this.host = host;
            return this;
        }

        @Override
        public Builder port(final String port) {
            this.port = port;
            return this;
        }

        @Override
        public DatabaseInfo build() {
            return new DatabaseInfo(system, url, user, db, host, port);
        }
    }

    public String getSystem() {
        return system;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getDb() {
        return db;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }
}
