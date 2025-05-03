@file:Suppress("ktlint:standard:no-empty-file")

package io.github.kingg22.api.vacunas.panama.configuration

/*
/**
 * Configuración principal de caché reactiva utilizando Redis.
 *
 * Esta clase configura las plantillas de Redis y la serialización de objetos dentro del contexto reactivo.
 * Los métodos proporcionan la configuración de serialización, la creación de un [ReactiveRedisTemplate] y
 * la configuración de un [org.springframework.cache.CacheManager] reactivo para la gestión de caché.
 *
 * La configuración está optimizada para manejar objetos en formato JSON usando Jackson, y se integra con Spring
 * Cache para permitir la anotación de métodos con `@Cacheable`.
 */
TODO Probably this file will be delete
@Configuration
@EnableCaching
class ReactiveRedisConfiguration {
    /**
 * Configura el serializador de caché que usa Jackson para serializar/deserialize objetos a JSON.
 * Este serializador es utilizado en la configuración de caché de Redis.
 *
 * @param objectMapper el objeto ObjectMapper utilizado para serializar objetos.
 * @return un [GenericJackson2JsonRedisSerializer] configurado.
 */
    @Bean
    fun redisCacheSerializer(objectMapper: ObjectMapper) =
        GenericJackson2JsonRedisSerializer(objectMapper).configure { objectMapper ->
            objectMapper.registerModule(JavaTimeModule())
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }

    /**
 * Crea un [SerializationPair] utilizando un [RedisSerializer] específico de [Object].
 * Este par de serialización es utilizado en la configuración de caché para definir cómo se serializan los valores.
 *
 * @param serializer el serializador de Redis.
 * @return un [SerializationPair] para la configuración de caché.
 */
    @Bean
    fun retrieveRedisCacheAsSerializationPair(serializer: RedisSerializer<Object>) =
        SerializationPair.fromSerializer(serializer)

    /**
 * Proporciona la configuración predeterminada para Redis Cache con un [SerializationPair].
 * Se utiliza para definir cómo se serializan los valores de caché en Redis.
 *
 * @param serializationPair la configuración de serialización de valores.
 * @return una instancia de [RedisCacheConfiguration] con la configuración de serialización.
 */
    @Bean
    fun redisCacheConfigurationDefault(serializationPair: SerializationPair<Object>) =
        RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(serializationPair)

    /**
 * Configura un [ReactiveRedisTemplate] que es responsable de interactuar con Redis de manera reactiva.
 * Usa el [ReactiveRedisConnectionFactory] para la conexión reactiva y el serializador para los valores.
 *
 * @param reactiveRedisConnectionFactory la fábrica de conexión reactiva de Redis.
 * @param serializer el serializador para los valores de la plantilla de Redis.
 * @return un [ReactiveRedisTemplate] configurado para operaciones reactivas de tipo [String] -> [kotlin.io.Serializable]
 */
    @Bean
    fun retrieveReactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
        serializer: RedisSerializer<Object>,
    ) = ReactiveRedisTemplate<String, Serializable>(
        reactiveRedisConnectionFactory,
        newSerializationContext<String, Serializable>(serializer).build(),
    )

    /**
 * Configura el [org.springframework.cache.CacheManager] de Redis con la configuración predeterminada y las configuraciones iniciales de caché.
 * Este `CacheManager` administra las cachés en la aplicación.
 *
 * @param cacheDefault configuración predeterminada de caché.
 * @return un `CacheManager` que utiliza la configuración de Redis y la personalización de caché.
 */
    @Bean
    fun retrieveRedisCacheManagerBuilder(cacheDefault: RedisCacheConfiguration) = RedisCacheManagerBuilderCustomizer {
        it.cacheDefaults(cacheDefault)
        it.withInitialCacheConfigurations(
            CacheDuration.asMapKeyRedisCacheConfig(cacheDefault),
        )
    }
}
*/
