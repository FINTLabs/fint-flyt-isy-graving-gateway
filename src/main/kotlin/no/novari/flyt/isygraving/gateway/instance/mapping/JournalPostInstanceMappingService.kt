package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.gateway.webinstance.InstanceMapper
import no.novari.flyt.gateway.webinstance.model.File
import no.novari.flyt.gateway.webinstance.model.instance.InstanceObject
import no.novari.flyt.isygraving.gateway.instance.model.Document
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import no.novari.flyt.isygraving.gateway.instance.model.Recipient
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class JournalPostInstanceMappingService : InstanceMapper<JournalPostInstance> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: JournalPostInstance,
        persistFile: (File) -> UUID,
    ): InstanceObject =
        incomingInstance.journalEntries.first().let { entry ->
            val (mainDocument, attachments) = splitDocuments(entry.documents)

            InstanceObject(
                valuePerKey =
                    mapOf(
                        "archiveCaseId" to incomingInstance.archiveCaseId,
                        "tenant" to incomingInstance.tenant,
                        "caseId" to incomingInstance.caseId,
                        "caseArchiveGuid" to incomingInstance.caseArchiveGuid,
                        "municipalityName" to incomingInstance.municipalityName,
                        "caseType" to incomingInstance.caseType,
                        "locationReference" to incomingInstance.locationReference,
                        "caseDate" to incomingInstance.caseDate,
                        "caseResponsible" to incomingInstance.caseResponsible,
                        "status" to incomingInstance.status,
                        "journalMunicipalityName" to entry.municipalityName,
                        "journalCaseType" to entry.caseType,
                        "journalLocationReference" to entry.locationReference,
                        "journalDate" to entry.date,
                        "documentType" to entry.documentType,
                        "caseHandler" to entry.caseHandler,
                        "mainDocumentTitle" to mainDocument.title,
                        "mainDocumentFileName" to mainDocument.fileName,
                        "mainDocumentLastModified" to mainDocument.lastModified,
                        "mainDocumentStatus" to mainDocument.status,
                        "mainDocumentMediaType" to mainDocument.mediaType,
                        "mainDocumentBase64" to mainDocument.documentBase64,
                    ),
                objectCollectionPerKey =
                    mutableMapOf(
                        "recipients" to entry.recipients.map(::mapRecipient),
                        "attachments" to attachments.map(::mapAttachment),
                    ),
            )
        }

    private fun splitDocuments(documents: List<Document>): Pair<Document, List<Document>> {
        val mainDocument =
            documents.firstOrNull { it.mainDocument }
                ?: throw MissingMainDocumentException("Main document is required but none of the documents are flagged as mainDocument")
        val attachments = documents.filterNot { it.mainDocument }
        return mainDocument to attachments
    }

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

    private fun mapAttachment(document: Document): InstanceObject =
        InstanceObject(
            valuePerKey =
                mapOf(
                    "title" to document.title,
                    "fileName" to document.fileName,
                    "lastModified" to document.lastModified,
                    "status" to document.status,
                    "mediaType" to document.mediaType,
                    "documentBase64" to document.documentBase64,
                ),
        )
}
