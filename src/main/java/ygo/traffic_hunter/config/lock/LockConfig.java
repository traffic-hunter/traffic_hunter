package ygo.traffic_hunter.config.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {

    @Bean
    public ReadWriteLock readWriteLock() {
        return new ReentrantReadWriteLock();
    }

    @Bean
    public ReentrantLock reentrantLock() {
        return new ReentrantLock();
    }
}
