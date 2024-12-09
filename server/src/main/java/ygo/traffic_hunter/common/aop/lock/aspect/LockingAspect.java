package ygo.traffic_hunter.common.aop.lock.aspect;

import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ygo.traffic_hunter.common.aop.lock.method.LockMode;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LockingAspect {

    private final ReadWriteLock readWriteLock;

    @Around("@annotation(ygo.traffic_hunter.common.aop.lock.Lock) && args(lockMode)")
    public Object lock(final ProceedingJoinPoint joinPoint, final LockMode lockMode) throws Throwable {
        if(Objects.equals(lockMode, LockMode.READ)) {
            return readLock(joinPoint);
        } else {
            return writeLock(joinPoint);
        }
    }

    private Object readLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        readWriteLock.readLock().lock();

        try {
            return joinPoint.proceed();
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private Object writeLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        readWriteLock.writeLock().lock();

        try {
            return joinPoint.proceed();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
