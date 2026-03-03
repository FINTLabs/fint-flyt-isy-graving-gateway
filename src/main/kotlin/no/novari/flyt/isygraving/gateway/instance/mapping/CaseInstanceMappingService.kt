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
                    "caseId" to incomingInstance.caseId,
                    "caseArchiveGuid" to incomingInstance.caseArchiveGuid,
                    "tenant" to incomingInstance.tenant,
                    "municipalityName" to incomingInstance.municipalityName,
                    "caseType" to incomingInstance.caseType,
                    "businessArea" to incomingInstance.businessArea,
                    "businessAreaType" to incomingInstance.businessAreaType,
                    "locationReference" to incomingInstance.locationReference,
                    "locationReferenceFormatted" to incomingInstance.locationReferenceFormatted,
                    "caseDate" to incomingInstance.caseDate,
                    "caseYear" to incomingInstance.caseYear,
                    "caseResponsible" to incomingInstance.caseResponsible,
                    "status" to incomingInstance.status,
                    "statusName" to incomingInstance.statusName,
                ),
        )
}
