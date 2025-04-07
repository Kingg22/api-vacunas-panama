package io.github.kingg22.api.vacunas.panama

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class ApiVacunasPanamaApplicationTests {
    @Test
    fun contextLoads() {}
}
