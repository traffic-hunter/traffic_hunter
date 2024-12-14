/**
 * The MIT License
 *
 * Copyright (c) 2024 yungwang-o
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
