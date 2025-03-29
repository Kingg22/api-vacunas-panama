package io.github.kingg22.api.vacunas.panama.configuration

import org.springframework.data.redis.cache.RedisCacheConfiguration
import java.time.Duration

enum class CacheDuration(private val ttl: Duration) {
    TINY(Duration.ofSeconds(30)),
    SHORT(Duration.ofMinutes(1)),
    CACHE(Duration.ofMinutes(5)),
    MEDIUM(Duration.ofMinutes(30)),
    LARGE(Duration.ofHours(1)),
    EXTRA_LARGE(Duration.ofHours(2)),
    HUGE(Duration.ofDays(1)),
    MASSIVE(Duration.ofDays(30)),
    ;

    /** Obtener la configuración de caché con TTL */
    fun getCacheConfig(defaultCacheConfig: RedisCacheConfiguration) = defaultCacheConfig.entryTtl(ttl)

    companion object {
        /**
         * Obtener todas las configuraciones como [Map] con su [RedisCacheConfiguration].
         *
         * Listo para [org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder.withInitialCacheConfigurations]
         */
        internal fun asMapKeyRedisCacheConfig(redisCacheConfiguration: RedisCacheConfiguration) =
            entries.associate { Pair(it.name, it.getCacheConfig(redisCacheConfiguration)) }

        fun asMap() = entries.associate { Pair(it.name, it.ttl) }
    }
}
