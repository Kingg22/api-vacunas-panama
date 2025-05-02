package io.github.kingg22.api.vacunas.panama

import com.redis.testcontainers.RedisContainer
import org.junit.Rule
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

// Maybe this file will be deleted
class TestContainersConfig {
    @Rule
    fun redisContainer(): RedisContainer = RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME)

    @Rule
    fun postgresContainer(): PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
}
