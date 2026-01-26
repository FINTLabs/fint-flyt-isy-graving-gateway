package no.novari.flyt.isygraving.gateway.dispatch

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfiguration {
    @Bean
    fun restClient(
        builder: RestClient.Builder,
        @Value("\${novari.flyt.isy-graving.dispatch.api-key-header:X-API-KEY}") apiKeyHeader: String,
        @Value("\${novari.flyt.isy-graving.dispatch.api-key:}") apiKey: String,
    ): RestClient {
        val apiKeyInterceptor =
            ClientHttpRequestInterceptor { request, body, execution ->
                if (apiKey.isNotBlank()) {
                    request.headers.set(apiKeyHeader, apiKey)
                }
                execution.execute(request, body)
            }

        return builder.requestInterceptor(apiKeyInterceptor).build()
    }
}
