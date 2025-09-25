package no.fintlabs.instance.gateway

import no.fintlabs.gateway.webinstance.InstanceProcessor
import no.fintlabs.gateway.webinstance.InstanceProcessorFactoryService
import no.fintlabs.instance.gateway.mapping.SakMappingService
import no.fintlabs.instance.gateway.model.Sak
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SakInstanceProcessorConfiguration {
    @Bean(name = ["sakInstanceProcessor"])
    fun sakInstanceProcessor(
        instanceProcessorFactoryService: InstanceProcessorFactoryService,
        sakMappingService: SakMappingService,
    ): InstanceProcessor<Sak> {
        val idFunction: (Sak) -> String = { s ->
            s.saksId
        }

        return instanceProcessorFactoryService.createInstanceProcessor(
            "sak",
            idFunction,
            sakMappingService,
        )
    }
}
