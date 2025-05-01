package io.github.kingg22.api.vacunas.panama

import com.redis.testcontainers.RedisContainer
import org.junit.Rule
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
@ActiveProfiles("test")
class TestContainersConfig {
    @Bean
    @Rule
    @ServiceConnection(name = "redis")
    fun redisContainer(): RedisContainer = RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME)

    @Bean
    @Rule
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
}
