package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.gateway.webinstance.InstanceMapper
import no.novari.flyt.gateway.webinstance.model.File
import no.novari.flyt.gateway.webinstance.model.instance.InstanceObject
import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CaseInstanceMappingService : InstanceMapper<CaseInstance> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: CaseInstance,
        persistFile: (File) -> UUID,
    ): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "tenant" to incomingInstance.tenant,
                    "caseId" to incomingInstance.caseId,
                    "caseType" to incomingInstance.caseType,
                    "businessArea" to incomingInstance.businessArea,
                    "businessAreaType" to incomingInstance.businessAreaType,
                    "caseArchiveGuid" to incomingInstance.caseArchiveGuid,
                    "municipalityName" to incomingInstance.municipalityName,
                    "locationReference" to incomingInstance.locationReference,
                    "locationReferenceFull" to incomingInstance.locationReferenceFull,
                    "locationReferenceFormatted" to incomingInstance.locationReferenceFormatted,
                    "streetName" to incomingInstance.streetName,
                    "caseDate" to incomingInstance.caseDate,
                    "caseYear" to incomingInstance.caseYear,
                    "caseResponsible" to incomingInstance.caseResponsible,
                    "status" to incomingInstance.status,
                    "statusName" to incomingInstance.statusName,
                ),
        )
}
