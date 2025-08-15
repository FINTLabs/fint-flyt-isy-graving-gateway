package no.fintlabs.instance.gateway

import java.util.Optional
import no.fintlabs.gateway.webinstance.InstanceProcessor
import no.fintlabs.gateway.webinstance.InstanceProcessorFactoryService
import no.fintlabs.instance.gateway.extension.toId
import no.fintlabs.instance.gateway.mapping.JournalpostInstantMappingService
import no.fintlabs.instance.gateway.model.Journalpost
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JournalpostInstanceProcessorConfiguration {

    @Bean(name = ["journalpostInstanceProcessor"])
    fun journalpostInstanceProcessor(
        instanceProcessorFactoryService: InstanceProcessorFactoryService,
        journalpostInstanceMappingService: JournalpostInstantMappingService
    ): InstanceProcessor<Journalpost> {
        val idFunction: (Journalpost) -> Optional<String> = { j: Journalpost ->
            Optional.ofNullable(j.saksnr.toId())
        }

        return instanceProcessorFactoryService.createInstanceProcessor(
            "journalpost",
            idFunction,
            journalpostInstanceMappingService
        )
    }
}
