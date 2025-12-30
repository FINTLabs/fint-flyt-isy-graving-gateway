package no.novari.flyt.isygraving.gateway.dispatch

import no.novari.flyt.gateway.webinstance.kafka.ArchiveCaseIdRequestService
import no.novari.flyt.kafka.instanceflow.headers.InstanceFlowHeaders
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class CaseDispatchService(
    private val restClient: RestClient,
    private val caseDispatchCache: CaseDispatchCache,
    private val archiveCaseIdRequestService: ArchiveCaseIdRequestService,
) {
    fun handleInstanceDispatched(instanceFlowHeaders: InstanceFlowHeaders) {
        val sourceApplicationIntegrationId = instanceFlowHeaders.sourceApplicationIntegrationId
        if (sourceApplicationIntegrationId != "case") {
            log.debug(
                "Skipping instance-dispatched for sourceApplicationIntegrationId={}",
                sourceApplicationIntegrationId,
            )
            return
        }

        val sourceApplicationId = instanceFlowHeaders.sourceApplicationId
        val caseId =
            instanceFlowHeaders.sourceApplicationInstanceId
                ?: error("Missing sourceApplicationInstanceId in instance-dispatched headers")

        val dispatchContext =
            caseDispatchCache.get(caseId)
                ?: error("Missing dispatch context for caseId=$caseId")

        val archiveCaseId =
            instanceFlowHeaders.archiveInstanceId
                ?: archiveCaseIdRequestService.getArchiveCaseId(sourceApplicationId, caseId)
                ?: error("Missing archiveCaseId for caseId=$caseId")

        val payload =
            CaseDispatchPayload(
                tenant = dispatchContext.tenant,
                caseId = caseId,
                caseArchiveGuid = dispatchContext.caseArchiveGuid,
                archiveCaseId = archiveCaseId,
            )

        log.info("Dispatching caseId={} to callback={}", caseId, dispatchContext.callbackUrl)

        restClient
            .put()
            .uri(dispatchContext.callbackUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(payload)
            .retrieve()
            .toBodilessEntity()

        caseDispatchCache.remove(caseId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CaseDispatchService::class.java)
    }
}
