package ygo.traffic_hunter.common.aop.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ygo.traffic_hunter.common.aop.lock.method.LockMode;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Lock {

    LockMode mode();
}
