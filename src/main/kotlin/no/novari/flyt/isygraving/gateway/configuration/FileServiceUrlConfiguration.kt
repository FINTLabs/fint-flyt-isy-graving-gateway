package no.novari.flyt.isygraving.gateway.configuration

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class FileServiceUrlConfiguration(
    @Value("\${novari.flyt.file-service-url:}") private val fileServiceUrl: String,
) {
    @PostConstruct
    fun verifyFileServiceUrl() {
        if (fileServiceUrl.isBlank()) {
            log.error("Missing required property: novari.flyt.file-service-url")
            error("Missing required property: novari.flyt.file-service-url")
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(FileServiceUrlConfiguration::class.java)
    }
}
