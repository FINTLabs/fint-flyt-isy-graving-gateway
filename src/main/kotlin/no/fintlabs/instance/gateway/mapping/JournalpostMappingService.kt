package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.DATO
import no.fintlabs.instance.gateway.mapping.InstanceKeys.DOKUMENTER
import no.fintlabs.instance.gateway.mapping.InstanceKeys.DOKUMENTTYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.KOMMUNENAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.MOTTAKERE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSBEHANDLER
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSTYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.STEDSREFERANSE
import no.fintlabs.instance.gateway.model.Journalpost
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class JournalpostMappingService(
    private val mottakerMappingService: MottakerMappingService,
    private val dokumenterMappingService: DokumenterMappingService,
) {
    fun map(
        sourceApplicationId: Long,
        saksId: String,
        persistFile: (File) -> UUID,
        journalpost: Journalpost,
    ): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty(KOMMUNENAVN, journalpost.kommunenavn)
                    putOrEmpty(SAKSTYPE, journalpost.sakstype)
                    putOrEmpty(STEDSREFERANSE, journalpost.stedsreferanse)
                    putOrEmpty(DATO, journalpost.dato)
                    putOrEmpty(DOKUMENTTYPE, journalpost.dokumenttype)
                    putOrEmpty(SAKSBEHANDLER, journalpost.saksbehandler)
                },
            objectCollectionPerKey =
                mutableMapOf(
                    MOTTAKERE to journalpost.mottakere.map { mottakerMappingService.map(it) },
                    DOKUMENTER to
                        dokumenterMappingService.map(
                            persistFile = persistFile,
                            sourceApplicationId = sourceApplicationId,
                            sourceApplicationInstanceId = saksId, // FIXME: Is this the correct ID?
                            dokumenter = journalpost.dokumenter,
                        ),
                ),
        )
    }
}
