package no.novari.flyt.isygraving.gateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import kotlin.test.assertEquals

class JacksonConfigurationTest {
    @Test
    fun `configures Jackson max string length for large base64 payloads`() {
        val builder = Jackson2ObjectMapperBuilder()

        JacksonConfiguration()
            .jacksonStreamReadConstraintsCustomizer(100_000_000)
            .customize(builder)

        val objectMapper = builder.build<ObjectMapper>()

        assertEquals(100_000_000, objectMapper.factory.streamReadConstraints().maxStringLength)
    }
}
