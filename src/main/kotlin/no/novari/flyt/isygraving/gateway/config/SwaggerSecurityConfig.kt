package no.novari.flyt.isygraving.gateway.config

import no.novari.flyt.webresourceserver.security.SecurityFilterChainFactoryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SwaggerSecurityConfig(
    private val securityFilterChainFactoryService: SecurityFilterChainFactoryService,
    private val environment: Environment,
) {
    private fun swaggerBasePath(): String {
        val configured = environment.getProperty("novari.flyt.isy-graving.swagger.base-path")
            ?: environment.getProperty("novari.flyt.isy-graving.api.base-path")
            ?: "/api/isygraving/instances"

        return if (configured.startsWith("/")) configured else "/$configured"
    }

    @Bean
    @Order(0)
    fun swaggerUiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        securityFilterChainFactoryService.permitAll(http, "${swaggerBasePath()}/swagger-ui")

    @Bean
    @Order(0)
    fun openApiDocsSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        securityFilterChainFactoryService.permitAll(http, "${swaggerBasePath()}/v3/api-docs")
}
