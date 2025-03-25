package org.traffichunter.javaagent.plugin.spring.business;

import io.opentelemetry.context.Context;
import java.lang.reflect.Method;
import org.traffichunter.javaagent.plugin.sdk.instumentation.Instrumentor;
import org.traffichunter.javaagent.plugin.sdk.instumentation.SpanScope;

public class SpringBusinessInstrumentationHelper {

    public static class Service {

        public static SpanScope start(final Method method, final Context parentContext) {

            return Instrumentor.startBuilder(method)
                    .instrumentationName("spring-business-service-inst")
                    .spanName(SpringBusinessInstrumentationHelper::generateSpanName)
                    .context(parentContext)
                    .start();
        }
    }

    public static class Repository {

        public static SpanScope start(final Method method, final Context parentContext) {

            return Instrumentor.startBuilder(method)
                    .instrumentationName("spring-business-repo-inst")
                    .spanName(SpringBusinessInstrumentationHelper::generateSpanName)
                    .context(parentContext)
                    .start();
        }
    }

    public static void end(final SpanScope spanScope, final Throwable throwable) {
        Instrumentor.end(spanScope, throwable);
    }

    private static String generateSpanName(final Method method) {

        try {
            return method.getName().split(" ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return method.getName();
        }
    }
}
