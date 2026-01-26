package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.gateway.webinstance.InstanceMapper
import no.novari.flyt.gateway.webinstance.model.File
import no.novari.flyt.gateway.webinstance.model.instance.InstanceObject
import no.novari.flyt.isygraving.gateway.instance.model.Document
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import no.novari.flyt.isygraving.gateway.instance.model.Recipient
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
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
            val sourceApplicationInstanceId = incomingInstance.caseId
            val mainDocumentFileId =
                persistFile(
                    toFile(
                        sourceApplicationId = sourceApplicationId,
                        sourceApplicationInstanceId = sourceApplicationInstanceId,
                        document = mainDocument,
                    ),
                )
            log.info(
                "Uploaded main document: sourceApplicationInstanceId={}, fileName={}, fileId={}",
                sourceApplicationInstanceId,
                mainDocument.fileName,
                mainDocumentFileId,
            )

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
                        "mainDocumentBase64" to mainDocumentFileId.toString(),
                    ),
                objectCollectionPerKey =
                    mutableMapOf(
                        "recipients" to entry.recipients.map(::mapRecipient),
                        "attachments" to
                            attachments.map {
                                mapAttachment(
                                    document = it,
                                    sourceApplicationId = sourceApplicationId,
                                    sourceApplicationInstanceId = sourceApplicationInstanceId,
                                    persistFile = persistFile,
                                )
                            },
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

    private fun mapAttachment(
        document: Document,
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        persistFile: (File) -> UUID,
    ): InstanceObject {
        val fileId =
            persistFile(
                toFile(
                    sourceApplicationId = sourceApplicationId,
                    sourceApplicationInstanceId = sourceApplicationInstanceId,
                    document = document,
                ),
            )
        log.info(
            "Uploaded attachment: sourceApplicationInstanceId={}, fileName={}, fileId={}",
            sourceApplicationInstanceId,
            document.fileName,
            fileId,
        )
        return InstanceObject(
            valuePerKey =
                mapOf(
                    "title" to document.title,
                    "fileName" to document.fileName,
                    "lastModified" to document.lastModified,
                    "status" to document.status,
                    "mediaType" to document.mediaType,
                    "documentBase64" to fileId.toString(),
                ),
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(JournalPostInstanceMappingService::class.java)
    }

    private fun toFile(
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        document: Document,
    ): File {
        val mediaType = MediaType.parseMediaType(document.mediaType)
        return File(
            name = document.fileName,
            type = mediaType,
            sourceApplicationId = sourceApplicationId,
            sourceApplicationInstanceId = sourceApplicationInstanceId,
            encoding = "UTF-8",
            base64Contents = document.documentBase64,
        )
    }
}
