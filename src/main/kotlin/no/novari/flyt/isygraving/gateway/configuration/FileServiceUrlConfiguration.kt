package no.novari.flyt.isygraving.gateway.configuration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Configuration

@Configuration
class FileServiceUrlConfiguration(
    @Value("\${novari.flyt.file-service-url:}") private val fileServiceUrl: String,
) : ApplicationRunner {
    override fun run(args: org.springframework.boot.ApplicationArguments?) {
        log.info("Resolved novari.flyt.file-service-url={}", fileServiceUrl)
        if (fileServiceUrl.isBlank()) {
            log.error("Missing required property: novari.flyt.file-service-url")
            error("Missing required property: novari.flyt.file-service-url")
        }
    }

    private companion object {
        private val log = LoggerFactory.getLogger(FileServiceUrlConfiguration::class.java)
    }
}
