package no.novari.flyt.isygraving.gateway.dispatch

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfiguration {
    @Bean
    fun restClient(builder: RestClient.Builder): RestClient = builder.build()
}
