package no.novari.flyt.isygraving.gateway.instance.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class ErrorHandler {

    @Bean
    @Order(0)
    fun errorFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http.securityMatcher("/error")
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .build()
    }
}