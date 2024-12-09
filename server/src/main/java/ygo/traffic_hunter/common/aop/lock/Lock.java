package ygo.traffic_hunter.common.aop.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ygo.traffic_hunter.common.aop.lock.method.LockMode;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Lock {

    LockMode mode();
}
