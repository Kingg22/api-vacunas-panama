package io.github.kingg22.api.vacunas.panama.configuration

import org.springframework.data.redis.cache.RedisCacheConfiguration
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

enum class CacheDuration(private val ttl: Duration, private val cacheNames: String) {
    /** With 30 seconds */
    TINY(30.seconds, "tiny"),

    /** With 1 minute */
    SHORT(1.minutes, "short"),

    /** With 5 minutos */
    CACHE(5.minutes, "cache"),

    /** With 30 minutes */
    MEDIUM(30.minutes, "medium"),

    /** With 1 hour */
    LARGE(1.hours, "large"),

    /** With 2 hours */
    EXTRA_LARGE(2.hours, "extra_large"),

    /** With 1 day */
    HUGE(1.days, "huge"),

    /** With 30 days */
    MASSIVE(30.days, "massive"),
    ;

    /** Obtener la configuración de caché con TTL */
    fun getCacheConfig(defaultCacheConfig: RedisCacheConfiguration) = defaultCacheConfig.entryTtl(ttl.toJavaDuration())

    companion object {
        /**
         * Obtener todas las configuraciones como [Map] con su [RedisCacheConfiguration].
         *
         * Listo para [org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder.withInitialCacheConfigurations]
         */
        internal fun asMapKeyRedisCacheConfig(redisCacheConfiguration: RedisCacheConfiguration) =
            entries.associate { it.cacheNames to it.getCacheConfig(redisCacheConfiguration) }

        /**
         * With 30 seconds
         * @see CacheDuration.TINY
         */
        const val TINY_VALUE = "tiny"

        /**
         * With 1 minute
         * @see CacheDuration.SHORT
         */
        const val SHORT_VALUE = "short"

        /**
         * With 5 minutos
         * @see CacheDuration.CACHE
         */
        const val CACHE_VALUE = "cache"

        /**
         * With 30 minutes
         * @see CacheDuration.MEDIUM
         */
        const val MEDIUM_VALUE = "medium"

        /**
         * With 1 hour
         * @see CacheDuration.LARGE
         */
        const val LARGE_VALUE = "large"

        /**
         * With 2 hours
         * @see CacheDuration.EXTRA_LARGE
         */
        const val EXTRA_LARGE_VALUE = "extra_large"

        /**
         * With 1 day
         * @see CacheDuration.HUGE
         * */
        const val HUGE_VALUE = "huge"

        /**
         * With 30 days
         * @see CacheDuration.MASSIVE
         * */
        const val MASSIVE_VALUE = "massive"
    }
}
