package org.traffichunter.javaagent.plugin.sdk.instumentation;

import static org.junit.jupiter.api.Assertions.*;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
@DisplayNameGeneration(ReplaceUnderscores.class)
class SpanScopeTest {

    @Test
    void is_check_span_scope_equal_span_scope_noop() {

        SpanScope noop = SpanScope.NOOP;
        SpanScope spanScope = SpanScope.create(Span.current(), Scope.noop());

        Assertions.assertEquals(noop, spanScope);
    }
}