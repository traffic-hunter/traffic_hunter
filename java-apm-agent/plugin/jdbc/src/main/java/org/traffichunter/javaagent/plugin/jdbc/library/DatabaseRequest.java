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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class DatabaseRequest {

    private final DatabaseInfo databaseInfo;

    private final String statementString;

    private DatabaseRequest(final DatabaseInfo databaseInfo, final String statementString) {
        this.databaseInfo = databaseInfo;
        this.statementString = statementString;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private DatabaseInfo databaseInfo;

        private String statementString;

        private Builder() {}

        public Builder dbInfo(final DatabaseInfo databaseInfo) {
            this.databaseInfo = databaseInfo;
            return this;
        }

        public Builder statementString(final String statementString) {
            this.statementString = statementString;
            return this;
        }

        public DatabaseRequest build() {
            return new DatabaseRequest(databaseInfo, statementString);
        }
    }

    public DatabaseInfo getDatabaseInfo() {
        return databaseInfo;
    }

    public String getStatementString() {
        return statementString;
    }

    public static DatabaseRequest create(final PreparedStatement statement) {
        return create(statement, JdbcData.prepareStatementInfo.get(statement));
    }

    public static DatabaseRequest create(final Statement statement, final String statementString) {
        try {
            Connection connection = statement.getConnection();

            if(Objects.isNull(connection)) {
                return null;
            }

            DatabaseInfo databaseInfo = JdbcData.connectionInfo.get(connection);

            return DatabaseRequest.builder()
                    .dbInfo(databaseInfo)
                    .statementString(statementString)
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException("Could not create database request : ", e);
        }
    }
}
