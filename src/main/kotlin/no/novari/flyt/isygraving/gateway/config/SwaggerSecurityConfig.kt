package no.novari.flyt.isygraving.gateway.config

import no.novari.flyt.webresourceserver.security.SecurityFilterChainFactoryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SwaggerSecurityConfig(
    private val securityFilterChainFactoryService: SecurityFilterChainFactoryService,
) {
    @Bean
    @Order(0)
    fun swaggerUiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        securityFilterChainFactoryService.permitAll(http, "/swagger-ui")

    @Bean
    @Order(0)
    fun openApiDocsSecurityFilterChain(http: HttpSecurity): SecurityFilterChain =
        securityFilterChainFactoryService.permitAll(http, "/v3/api-docs")
}
