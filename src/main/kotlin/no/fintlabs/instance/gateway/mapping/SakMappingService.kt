package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.InstanceMapper
import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.JOURNALPOST
import no.fintlabs.instance.gateway.mapping.InstanceKeys.KOMMUNENAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSANSVARLIG
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSDATO
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSTYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKS_ID
import no.fintlabs.instance.gateway.mapping.InstanceKeys.STEDSREFERANSE
import no.fintlabs.instance.gateway.model.Sak
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SakMappingService(
    private val journalpostMappingService: JournalpostMappingService,
) : InstanceMapper<Sak> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: Sak,
        persistFile: (File) -> UUID,
    ): InstanceObject =
        with(incomingInstance) {
            InstanceObject(
                valuePerKey =
                    buildMap {
                        putOrEmpty(SAKS_ID, saksId)
                        putOrEmpty(KOMMUNENAVN, kommunenavn)
                        putOrEmpty(SAKSTYPE, sakstype)
                        putOrEmpty(STEDSREFERANSE, stedsreferanse)
                        putOrEmpty(SAKSDATO, saksdato)
                        putOrEmpty(SAKSANSVARLIG, saksansvarlig)
                    },
                objectCollectionPerKey =
                    mutableMapOf(
                        JOURNALPOST to
                            journalposter.map {
                                journalpostMappingService.map(
                                    sourceApplicationId = sourceApplicationId,
                                    saksId = saksId,
                                    persistFile = persistFile,
                                    journalpost = it,
                                )
                            },
                    ),
            )
        }
}
