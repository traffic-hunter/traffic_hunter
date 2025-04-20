package org.traffichunter.javaagent.plugin.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author yungwang-o
 * @version 1.1.0
 */
class CallDepthTest {

    @Test
    void same_class_call_depth_inc_test() {

        CallDepth callDepth = CallDepth.forClass(String.class);
        callDepth.getAndIncrement();

        CallDepth callDepth2 = CallDepth.forClass(String.class);
        callDepth2.getAndIncrement();

        assertEquals(callDepth.getDepth(), 2);
    }

    @Test
    void not_the_same_class_call_depth_inc_test() {

        CallDepth callDepth = CallDepth.forClass(String.class);
        callDepth.getAndIncrement();

        CallDepth callDepth2 = CallDepth.forClass(Integer.class);
        callDepth2.getAndIncrement();

        int sum = callDepth.getDepth() + callDepth2.getDepth();
        assertEquals(sum, 2);
        assertEquals(callDepth.getDepth(), 1);
        assertEquals(callDepth2.getDepth(), 1);
    }
}