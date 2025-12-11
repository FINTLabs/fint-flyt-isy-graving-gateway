package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.gateway.webinstance.InstanceMapper
import no.novari.flyt.gateway.webinstance.model.File
import no.novari.flyt.gateway.webinstance.model.instance.InstanceObject
import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CaseMappingService : InstanceMapper<CaseInstance> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: CaseInstance,
        persistFile: (File) -> UUID,
    ): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "tenant" to incomingInstance.tenant,
                    "municipalityName" to incomingInstance.municipalityName,
                    "caseType" to incomingInstance.caseType,
                    "locationReference" to incomingInstance.locationReference,
                    "caseDate" to incomingInstance.caseDate,
                    "caseResponsible" to incomingInstance.caseResponsible,
                    "status" to incomingInstance.status,
                    "date" to incomingInstance.date,
                    "callback" to incomingInstance.callback,
                ),
        )
}
