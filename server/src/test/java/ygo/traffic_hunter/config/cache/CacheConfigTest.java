package ygo.traffic_hunter.config.cache;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class CacheConfigTest {

    @Test
    void caffeine_cache_expire_test_no_schedule() throws InterruptedException {

        String key = "k";
        String value = "v";

        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build();

        for(int i = 1; i <= 100; i++) {
            cache.put(key + i, value);
        }

        assertThat(cache.estimatedSize()).isEqualTo(100);
        Thread.sleep(2000);
        assertThat(cache.estimatedSize()).isEqualTo(100);
    }
}