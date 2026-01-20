package no.novari.flyt.isygraving.gateway.dispatch

import com.fasterxml.jackson.databind.ObjectMapper
import no.novari.flyt.gateway.webinstance.kafka.ArchiveCaseIdRequestService
import no.novari.flyt.isygraving.gateway.dispatch.DispatchContextService.Companion.INTEGRATION_CASE
import no.novari.flyt.isygraving.gateway.dispatch.DispatchContextService.Companion.INTEGRATION_JOURNALPOST
import no.novari.flyt.isygraving.gateway.dispatch.DispatchContextService.Companion.buildDispatchKey
import no.novari.flyt.isygraving.gateway.dispatch.model.DispatchReceiptEntity
import no.novari.flyt.isygraving.gateway.dispatch.repository.DispatchContextRepository
import no.novari.flyt.isygraving.gateway.dispatch.repository.DispatchReceiptRepository
import no.novari.flyt.kafka.instanceflow.headers.InstanceFlowHeaders
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty

@Service
@ConditionalOnProperty(
    prefix = "novari.flyt.isy-graving.dispatch",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
class CaseDispatchService(
    private val restClient: RestClient,
    private val dispatchContextRepository: DispatchContextRepository,
    private val dispatchReceiptRepository: DispatchReceiptRepository,
    private val archiveCaseIdRequestService: ArchiveCaseIdRequestService,
    private val objectMapper: ObjectMapper,
) {
    fun handleInstanceDispatched(instanceFlowHeaders: InstanceFlowHeaders) {
        val sourceApplicationIntegrationId = instanceFlowHeaders.sourceApplicationIntegrationId
        if (sourceApplicationIntegrationId != INTEGRATION_CASE && sourceApplicationIntegrationId != INTEGRATION_JOURNALPOST) {
            log.debug(
                "Skipping instance-dispatched for sourceApplicationIntegrationId={}",
                sourceApplicationIntegrationId,
            )
            return
        }

        val sourceApplicationInstanceId =
            instanceFlowHeaders.sourceApplicationInstanceId
                ?: error("Missing sourceApplicationInstanceId in instance-dispatched headers")

        val dispatchKey = buildDispatchKey(sourceApplicationIntegrationId, sourceApplicationInstanceId)
        val existingReceipt = dispatchReceiptRepository.findById(dispatchKey).orElse(null)
        if (existingReceipt != null) {
            dispatchReceipt(existingReceipt)
            return
        }

        val dispatchContext =
            dispatchContextRepository.findById(dispatchKey).orElse(null)
                ?: error("Missing dispatch context for sourceApplicationInstanceId=$sourceApplicationInstanceId")

        val payload =
            when (sourceApplicationIntegrationId) {
                INTEGRATION_CASE ->
                    buildCasePayload(
                        instanceFlowHeaders,
                        dispatchContext.caseId,
                        dispatchContext.caseArchiveGuid,
                        dispatchContext.tenant,
                    )
                INTEGRATION_JOURNALPOST ->
                    buildJournalPostPayload(
                        instanceFlowHeaders,
                        dispatchContext.caseId,
                        dispatchContext.caseArchiveGuid,
                        dispatchContext.tenant,
                    )
                else -> error("Unsupported sourceApplicationIntegrationId=$sourceApplicationIntegrationId")
            }

        val receipt =
            DispatchReceiptEntity(
                id = dispatchKey,
                sourceApplicationIntegrationId = sourceApplicationIntegrationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                callbackUrl = dispatchContext.callbackUrl,
                payload = objectMapper.writeValueAsString(payload),
            )

        dispatchReceiptRepository.save(receipt)
        dispatchContextRepository.delete(dispatchContext)
        dispatchReceipt(receipt)
    }

    @Scheduled(
        initialDelayString = "\${novari.flyt.isy-graving.dispatch.retry-initial-delay:5m}",
        fixedDelayString = "\${novari.flyt.isy-graving.dispatch.retry-fixed-delay:24h}",
    )
    fun retryFailedDispatches() {
        val pendingDispatches = dispatchReceiptRepository.findAll()
        if (pendingDispatches.isEmpty()) {
            return
        }

        log.info("Retrying {} dispatch receipts", pendingDispatches.size)
        pendingDispatches.forEach { receipt ->
            try {
                dispatchReceipt(receipt)
            } catch (ex: Exception) {
                log.warn("Retry failed for dispatchReceiptId={}", receipt.id, ex)
            }
        }
    }

    private fun buildCasePayload(
        instanceFlowHeaders: InstanceFlowHeaders,
        caseId: String,
        caseArchiveGuid: String,
        tenant: String,
    ): CaseDispatchPayload {
        val sourceApplicationId = instanceFlowHeaders.sourceApplicationId
        val archiveCaseId =
            instanceFlowHeaders.archiveInstanceId
                ?: archiveCaseIdRequestService.getArchiveCaseId(sourceApplicationId, caseId)
                ?: error("Missing archiveCaseId for caseId=$caseId")

        return CaseDispatchPayload(
            tenant = tenant,
            caseId = caseId,
            caseArchiveGuid = caseArchiveGuid,
            archiveCaseId = archiveCaseId,
        )
    }

    private fun buildJournalPostPayload(
        instanceFlowHeaders: InstanceFlowHeaders,
        caseId: String,
        caseArchiveGuid: String,
        tenant: String,
    ): CaseDispatchPayload {
        val archiveCaseId =
            instanceFlowHeaders.archiveInstanceId
                ?: error("Missing archiveInstanceId for journalpost caseId=$caseId")

        return CaseDispatchPayload(
            tenant = tenant,
            caseId = caseId,
            caseArchiveGuid = caseArchiveGuid,
            archiveCaseId = archiveCaseId,
        )
    }

    private fun dispatchReceipt(receipt: DispatchReceiptEntity) {
        log.info(
            "Dispatching sourceApplicationInstanceId={} to callback={}",
            receipt.sourceApplicationInstanceId,
            receipt.callbackUrl,
        )

        val payloadNode = objectMapper.readTree(receipt.payload)

        restClient
            .put()
            .uri(receipt.callbackUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .body(payloadNode)
            .retrieve()
            .toBodilessEntity()

        dispatchReceiptRepository.delete(receipt)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CaseDispatchService::class.java)
    }
}
