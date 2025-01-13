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
package org.traffichunter.javaagent.plugin.hibernate.helper;

import java.util.function.Function;
import org.hibernate.SharedSessionContract;
import org.hibernate.internal.SessionImpl;
import org.hibernate.internal.StatelessSessionImpl;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
public class EntityNameHooker {

    private EntityNameHooker() {}

    public static String ejectEntityName(final String descriptor,
                                         final Object arg0,
                                         final Object arg1,
                                         final Function<Object, String> nameFromEntity) {

        String entityName = null;

        // save(String entityName, Object object)
        if(descriptor.startsWith("(Ljava/lang/String;Ljava/lang/Object;")) {
            entityName = arg0 == null ? nameFromEntity.apply(arg1) : (String) arg0;

            // save(Object obj)
        } else if(descriptor.startsWith("(Ljava/lang/Object;")) {
            entityName = nameFromEntity.apply(arg0);

            // get(String entityName, Serializable id)
        } else if(descriptor.startsWith("(Ljava/lang/String;")) {
            entityName = (String) arg0;

            // get(Class entityClass, Serializable id)
        } else if(descriptor.startsWith("(Ljava/lang/Class;") && arg0 != null) {
            entityName = ((Class<?>) arg0).getName();
        }

        return entityName;
    }

    private static String bestGuessEntityName(final SharedSessionContract session, final Object entity) {
        if (entity == null) {
            return null;
        }

        if (session instanceof SessionImpl) {
            return ((SessionImpl) session).bestGuessEntityName(entity);
        } else if (session instanceof StatelessSessionImpl) {
            return ((StatelessSessionImpl) session).bestGuessEntityName(entity);
        }

        return null;
    }

    public static Function<Object, String> bestGuessEntityName(final SharedSessionContract session) {
        return (entity) -> bestGuessEntityName(session, entity);
    }
}
