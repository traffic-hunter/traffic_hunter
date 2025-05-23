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
package ygo.traffic_hunter.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yungwang-o
 * @version 1.0.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public List<CaffeineCache> caffeineCaches() {
        return Arrays.stream(CacheType.values())
                .map(this::getCaffeineCache)
                .toList();
    }

    @Bean
    public CacheManager cacheManager(final List<CaffeineCache> caffeineCaches) {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(caffeineCaches);

        return cacheManager;
    }

    private CaffeineCache getCaffeineCache(final CacheType cacheType) {
        return new CaffeineCache(
                cacheType.cacheName,
                Caffeine.newBuilder()
                        .maximumSize(cacheType.maximumSize)
                        .scheduler(Scheduler.systemScheduler())
                        .expireAfterWrite(cacheType.expireAfterWrite, TimeUnit.SECONDS)
                        .build()
        );
    }

    @Getter
    public enum CacheType {

        AGENT_CACHE("agent_cache", 3600, 100),
        STATISTIC_TRANSACTION_PAGE_CACHE("statistic_transaction_page_cache", 600, 50),
        ALARM_CACHE("alarm_cache", 600, 50),
        THRESHOLD_CACHE("threshold_cache", 3600, 50),
        MEMBER_CACHE("member_cache", 3600, 50);

        private final String cacheName;
        private final int expireAfterWrite;
        private final int maximumSize;

        public static final String AGENT_CACHE_NAME = "agent_cache";
        public static final String STATISTIC_TRANSACTION_PAGE_CACHE_NAME = "statistic_transaction_page_cache";
        public static final String ALARM_CACHE_NAME = "alarm_cache";
        public static final String MEMBER_CACHE_NAME = "member_cache";
        public static final String THRESHOLD_CACHE_NAME = "threshold_cache";

        CacheType(final String cacheName, final int expireAfterWrite, final int maximumSize) {
            this.cacheName = cacheName;
            this.expireAfterWrite = expireAfterWrite;
            this.maximumSize = maximumSize;
        }
    }
}
