package no.novari.flyt.isygraving.gateway.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "ISY Graving Gateway API",
        description = "External API for submitting ISY Graving cases and journal posts.",
        version = "v1",
    ),
)
class OpenApiConfig
