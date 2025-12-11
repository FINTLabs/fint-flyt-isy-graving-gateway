package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.gateway.webinstance.InstanceMapper
import no.novari.flyt.gateway.webinstance.model.File
import no.novari.flyt.gateway.webinstance.model.instance.InstanceObject
import no.novari.flyt.isygraving.gateway.instance.model.Document
import no.novari.flyt.isygraving.gateway.instance.model.JournalEntry
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import no.novari.flyt.isygraving.gateway.instance.model.Recipient
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class JournalPostMappingService : InstanceMapper<JournalPostInstance> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: JournalPostInstance,
        persistFile: (File) -> UUID,
    ): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "archiveCaseId" to incomingInstance.archiveCaseId,
                    "callback" to incomingInstance.callback,
                ),
            objectCollectionPerKey =
                mutableMapOf(
                    "journalEntries" to incomingInstance.journalEntries.map(::mapJournalEntry),
                ),
        )

    private fun mapJournalEntry(entry: JournalEntry): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "municipalityName" to entry.municipalityName,
                    "caseType" to entry.caseType,
                    "locationReference" to entry.locationReference,
                    "date" to entry.date,
                    "documentType" to entry.documentType,
                    "caseHandler" to entry.caseHandler,
                ),
            objectCollectionPerKey =
                mutableMapOf(
                    "recipients" to entry.recipients.map(::mapRecipient),
                    "documents" to entry.documents.map(::mapDocument),
                ),
        )

    private fun mapRecipient(recipient: Recipient): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "name" to recipient.name,
                    "address" to recipient.address,
                    "postalCode" to recipient.postalCode,
                    "organizationNumber" to recipient.organizationNumber,
                ),
        )

    private fun mapDocument(document: Document): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "title" to document.title,
                    "fileName" to document.fileName,
                    "mainDocument" to document.mainDocument.toString(),
                    "lastModified" to document.lastModified,
                    "status" to document.status,
                    "mediaType" to document.mediaType,
                    "documentBase64" to document.documentBase64,
                ),
        )
}
