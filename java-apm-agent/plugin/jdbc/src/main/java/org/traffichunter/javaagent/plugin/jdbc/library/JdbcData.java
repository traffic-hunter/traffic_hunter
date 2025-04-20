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

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.traffichunter.javaagent.plugin.sdk.field.PluginSupportField;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class JdbcData {

    private static final Map<DatabaseInfo, WeakReference<DatabaseInfo>> map = new WeakHashMap<>();

    private static final ReentrantLock lock = new ReentrantLock();

    public static final PluginSupportField<Connection, DatabaseInfo> connectionInfo =
            PluginSupportField.find(Connection.class, DatabaseInfo.class);

    public static final PluginSupportField<PreparedStatement, String> prepareStatementInfo =
            PluginSupportField.find(PreparedStatement.class, String.class);

    private JdbcData() {}

    public static DatabaseInfo canonicalize(final DatabaseInfo databaseInfo) {

        lock.lock();
        try {

            WeakReference<DatabaseInfo> reference = map.get(databaseInfo);
            if(reference != null) {
                DatabaseInfo result = reference.get();
                if(result != null) {
                    return result;
                }
            }

            map.put(databaseInfo, new WeakReference<>(databaseInfo));
            return databaseInfo;
        } finally {
            lock.unlock();
        }
    }
}
