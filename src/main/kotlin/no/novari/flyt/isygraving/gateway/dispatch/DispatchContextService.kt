package no.novari.flyt.isygraving.gateway.dispatch

import no.novari.flyt.isygraving.gateway.dispatch.model.DispatchContextEntity
import no.novari.flyt.isygraving.gateway.dispatch.repository.DispatchContextRepository
import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import org.springframework.stereotype.Service

@Service
class DispatchContextService(
    private val dispatchContextRepository: DispatchContextRepository,
) {
    fun save(caseInstance: CaseInstance) {
        val id = buildDispatchKey(INTEGRATION_CASE, caseInstance.caseId)
        dispatchContextRepository.save(
            DispatchContextEntity(
                id = id,
                sourceApplicationIntegrationId = INTEGRATION_CASE,
                sourceApplicationInstanceId = caseInstance.caseId,
                tenant = caseInstance.tenant,
                caseId = caseInstance.caseId,
                caseArchiveGuid = caseInstance.caseArchiveGuid,
                callbackUrl = caseInstance.callback,
            ),
        )
    }

    fun save(journalPostInstance: JournalPostInstance) {
        val id = buildDispatchKey(INTEGRATION_JOURNALPOST, journalPostInstance.caseId)
        dispatchContextRepository.save(
            DispatchContextEntity(
                id = id,
                sourceApplicationIntegrationId = INTEGRATION_JOURNALPOST,
                sourceApplicationInstanceId = journalPostInstance.caseId,
                tenant = journalPostInstance.tenant,
                caseId = journalPostInstance.caseId,
                caseArchiveGuid = journalPostInstance.caseArchiveGuid,
                callbackUrl = journalPostInstance.callback,
            ),
        )
    }

    companion object {
        const val INTEGRATION_CASE = "case"
        const val INTEGRATION_JOURNALPOST = "journalpost"

        fun buildDispatchKey(
            sourceApplicationIntegrationId: String,
            sourceApplicationInstanceId: String,
        ): String = "$sourceApplicationIntegrationId:$sourceApplicationInstanceId"
    }
}
