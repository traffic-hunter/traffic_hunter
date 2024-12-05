package ygo.traffic_hunter.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                        .expireAfterWrite(cacheType.expireAfterWrite, TimeUnit.SECONDS)
                        .build()
        );
    }

    public enum CacheType {

        AGENT_CACHE("agent_cache", Integer.MAX_VALUE, 100),
        ;

        private final String cacheName;
        private final int expireAfterWrite;
        private final int maximumSize;

        public static final String AGENT_CACHE_NAME = "agent_cache";

        CacheType(final String cacheName, final int expireAfterWrite, final int maximumSize) {
            this.cacheName = cacheName;
            this.expireAfterWrite = expireAfterWrite;
            this.maximumSize = maximumSize;
        }
    }
}
