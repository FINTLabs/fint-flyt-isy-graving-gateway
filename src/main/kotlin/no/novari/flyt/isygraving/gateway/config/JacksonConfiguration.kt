package no.novari.flyt.isygraving.gateway.config

import com.fasterxml.jackson.core.StreamReadConstraints
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfiguration {
    @Bean
    fun jacksonStreamReadConstraintsCustomizer(
        @Value("\${novari.flyt.isy-graving.json.max-string-length:100000000}") maxStringLength: Int,
    ): Jackson2ObjectMapperBuilderCustomizer =
        Jackson2ObjectMapperBuilderCustomizer { builder ->
            builder.postConfigurer { objectMapper ->
                objectMapper.factory.setStreamReadConstraints(
                    StreamReadConstraints
                        .builder()
                        .maxStringLength(maxStringLength)
                        .build(),
                )
            }
        }
}
