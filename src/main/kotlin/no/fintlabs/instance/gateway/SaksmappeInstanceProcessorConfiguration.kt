package no.fintlabs.instance.gateway

import no.fintlabs.gateway.webinstance.InstanceProcessor
import no.fintlabs.gateway.webinstance.InstanceProcessorFactoryService
import no.fintlabs.instance.gateway.mapping.SakMappingService
import no.fintlabs.instance.gateway.model.Saksmappe
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SaksmappeInstanceProcessorConfiguration {
    @Bean(name = ["saksmappeInstanceProcessor"])
    fun saksmappeInstanceProcessor(
        instanceProcessorFactoryService: InstanceProcessorFactoryService,
        saksmappeInstanceMappingService: SakMappingService,
    ): InstanceProcessor<Saksmappe> {
        val idFunction: (Saksmappe) -> java.util.Optional<String> = { s: Saksmappe ->
            java.util.Optional.ofNullable(s.sysId)
        }

        return instanceProcessorFactoryService.createInstanceProcessor(
            "saksmappe",
            idFunction,
            saksmappeInstanceMappingService,
        )
    }
}
