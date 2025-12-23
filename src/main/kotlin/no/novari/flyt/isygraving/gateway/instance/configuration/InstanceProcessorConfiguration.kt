package no.novari.flyt.isygraving.gateway.instance.configuration

import no.novari.flyt.gateway.webinstance.InstanceProcessor
import no.novari.flyt.gateway.webinstance.InstanceProcessorFactoryService
import no.novari.flyt.isygraving.gateway.instance.mapping.CaseInstanceMappingService
import no.novari.flyt.isygraving.gateway.instance.mapping.JournalPostInstanceMappingService
import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InstanceProcessorConfiguration {
    @Bean
    fun caseInstanceProcessor(
        instanceProcessorFactoryService: InstanceProcessorFactoryService,
        caseInstanceMappingService: CaseInstanceMappingService,
    ): InstanceProcessor<CaseInstance> =
        instanceProcessorFactoryService.createInstanceProcessor(
            { _ -> "case" },
            { case -> case.caseId },
            caseInstanceMappingService,
        )

    @Bean
    fun journalPostInstanceProcessor(
        instanceProcessorFactoryService: InstanceProcessorFactoryService,
        journalPostMappingService: JournalPostInstanceMappingService,
    ): InstanceProcessor<JournalPostInstance> =
        instanceProcessorFactoryService.createInstanceProcessor(
            { _ -> "journalpost" },
            { journalPost -> journalPost.caseId },
            journalPostMappingService,
        )
}
