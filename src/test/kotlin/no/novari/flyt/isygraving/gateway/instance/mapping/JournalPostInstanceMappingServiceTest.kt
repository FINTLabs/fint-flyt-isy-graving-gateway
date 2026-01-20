package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.isygraving.gateway.instance.model.Document
import no.novari.flyt.isygraving.gateway.instance.model.JournalEntry
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import no.novari.flyt.isygraving.gateway.instance.model.Recipient
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class JournalPostInstanceMappingServiceTest {
    private val mappingService = JournalPostInstanceMappingService()

    @Test
    fun `maps journal post instance with new top-level fields`() {
        val instance = buildJournalPostInstance()

        val result =
            mappingService.map(
                sourceApplicationId = 7L,
                incomingInstance = instance,
            ) { _ -> UUID.randomUUID() }

        assertEquals(ARCHIVE_CASE_ID, result.valuePerKey["archiveCaseId"])
        assertEquals(TENANT, result.valuePerKey["tenant"])
        assertEquals(CASE_ID, result.valuePerKey["caseId"])
        assertEquals(CASE_ARCHIVE_GUID, result.valuePerKey["caseArchiveGuid"])
        assertEquals(MUNICIPALITY_NAME, result.valuePerKey["municipalityName"])
        assertEquals(CASE_TYPE, result.valuePerKey["caseType"])
        assertEquals(LOCATION_REFERENCE, result.valuePerKey["locationReference"])
        assertEquals(CASE_DATE, result.valuePerKey["caseDate"])
        assertEquals(CASE_RESPONSIBLE, result.valuePerKey["caseResponsible"])
        assertEquals(STATUS, result.valuePerKey["status"])
        assertEquals(ALT_MUNICIPALITY_NAME, result.valuePerKey["journalMunicipalityName"])
        assertEquals(ALT_CASE_TYPE, result.valuePerKey["journalCaseType"])
        assertEquals(ALT_LOCATION_REFERENCE, result.valuePerKey["journalLocationReference"])
        assertEquals(ALT_JOURNAL_DATE, result.valuePerKey["journalDate"])
        assertEquals(ALT_DOCUMENT_TYPE, result.valuePerKey["documentType"])
        assertEquals(ALT_CASE_HANDLER, result.valuePerKey["caseHandler"])
        assertEquals(DOCUMENT_TITLE, result.valuePerKey["mainDocumentTitle"])
        assertEquals(DOCUMENT_FILE_NAME, result.valuePerKey["mainDocumentFileName"])
        assertEquals(DOCUMENT_LAST_MODIFIED, result.valuePerKey["mainDocumentLastModified"])
        assertEquals(DOCUMENT_STATUS, result.valuePerKey["mainDocumentStatus"])
        assertEquals(DOCUMENT_MEDIA_TYPE, result.valuePerKey["mainDocumentMediaType"])
        assertEquals(DOCUMENT_BASE64, result.valuePerKey["mainDocumentBase64"])

        val recipient = assertNotNull(result.objectCollectionPerKey["recipients"]).single()
        assertEquals(RECIPIENT_NAME, recipient.valuePerKey["name"])
        assertEquals(RECIPIENT_ADDRESS, recipient.valuePerKey["address"])
        assertEquals(RECIPIENT_POSTAL_CODE, recipient.valuePerKey["postalCode"])
        assertEquals(RECIPIENT_ORG_NUMBER, recipient.valuePerKey["organizationNumber"])

        val attachment = assertNotNull(result.objectCollectionPerKey["attachments"]).single()
        assertEquals(ATTACHMENT_TITLE, attachment.valuePerKey["title"])
        assertEquals(ATTACHMENT_FILE_NAME, attachment.valuePerKey["fileName"])
        assertEquals(ATTACHMENT_LAST_MODIFIED, attachment.valuePerKey["lastModified"])
        assertEquals(ATTACHMENT_STATUS, attachment.valuePerKey["status"])
        assertEquals(ATTACHMENT_MEDIA_TYPE, attachment.valuePerKey["mediaType"])
        assertEquals(ATTACHMENT_BASE64, attachment.valuePerKey["documentBase64"])
    }

    @Test
    fun `throws when main document is missing`() {
        val instance =
            buildJournalPostInstance().copy(
                journalEntries =
                    listOf(
                        buildJournalEntry().copy(
                            documents =
                                listOf(
                                    buildAttachmentDocument(),
                                    buildAttachmentDocument(),
                                ),
                        ),
                    ),
            )

        assertFailsWith<MissingMainDocumentException> {
            mappingService.map(
                sourceApplicationId = 7L,
                incomingInstance = instance,
            ) { _ -> UUID.randomUUID() }
        }
    }

    private fun buildJournalPostInstance(): JournalPostInstance =
        JournalPostInstance(
            archiveCaseId = ARCHIVE_CASE_ID,
            journalEntries =
                listOf(
                    buildJournalEntry(),
                ),
            tenant = TENANT,
            caseId = CASE_ID,
            caseArchiveGuid = CASE_ARCHIVE_GUID,
            municipalityName = MUNICIPALITY_NAME,
            caseType = CASE_TYPE,
            locationReference = LOCATION_REFERENCE,
            caseDate = CASE_DATE,
            caseResponsible = CASE_RESPONSIBLE,
            status = STATUS,
            callback = CALLBACK,
        )

    private fun buildJournalEntry(): JournalEntry =
        JournalEntry(
            municipalityName = ALT_MUNICIPALITY_NAME,
            caseType = ALT_CASE_TYPE,
            locationReference = ALT_LOCATION_REFERENCE,
            date = ALT_JOURNAL_DATE,
            documentType = ALT_DOCUMENT_TYPE,
            caseHandler = ALT_CASE_HANDLER,
            recipients = listOf(buildRecipient()),
            documents = listOf(buildMainDocument(), buildAttachmentDocument()),
        )

    private fun buildRecipient(): Recipient =
        Recipient(
            name = RECIPIENT_NAME,
            address = RECIPIENT_ADDRESS,
            postalCode = RECIPIENT_POSTAL_CODE,
            organizationNumber = RECIPIENT_ORG_NUMBER,
        )

    private fun buildMainDocument(): Document =
        Document(
            title = DOCUMENT_TITLE,
            fileName = DOCUMENT_FILE_NAME,
            mainDocument = true,
            lastModified = DOCUMENT_LAST_MODIFIED,
            status = DOCUMENT_STATUS,
            mediaType = DOCUMENT_MEDIA_TYPE,
            documentBase64 = DOCUMENT_BASE64,
        )

    private fun buildAttachmentDocument(): Document =
        Document(
            title = ATTACHMENT_TITLE,
            fileName = ATTACHMENT_FILE_NAME,
            mainDocument = false,
            lastModified = ATTACHMENT_LAST_MODIFIED,
            status = ATTACHMENT_STATUS,
            mediaType = ATTACHMENT_MEDIA_TYPE,
            documentBase64 = ATTACHMENT_BASE64,
        )

    private companion object {
        const val ARCHIVE_CASE_ID = "archive-123"
        const val TENANT = "tenant-a"
        const val CASE_ID = "case-1"
        const val CASE_ARCHIVE_GUID = "guid-123"
        const val MUNICIPALITY_NAME = "Porsgrunn"
        const val CASE_TYPE = "Graving"
        const val LOCATION_REFERENCE = "Ref-1"
        const val CASE_DATE = "2024-01-01"
        const val CASE_RESPONSIBLE = "Responsible"
        const val STATUS = "OPEN"
        const val CALLBACK = "https://callback"
        const val RECIPIENT_NAME = "Recipient"
        const val RECIPIENT_ADDRESS = "Street 1"
        const val RECIPIENT_POSTAL_CODE = "0123"
        const val RECIPIENT_ORG_NUMBER = "999999999"
        const val DOCUMENT_TITLE = "Doc"
        const val DOCUMENT_FILE_NAME = "doc.pdf"
        const val DOCUMENT_LAST_MODIFIED = "2024-01-03"
        const val DOCUMENT_STATUS = "OK"
        const val DOCUMENT_MEDIA_TYPE = "application/pdf"
        const val DOCUMENT_BASE64 = "ZmlsZQ=="
        const val ATTACHMENT_TITLE = "Attachment"
        const val ATTACHMENT_FILE_NAME = "attach.pdf"
        const val ATTACHMENT_LAST_MODIFIED = "2024-01-04"
        const val ATTACHMENT_STATUS = "OK"
        const val ATTACHMENT_MEDIA_TYPE = "application/pdf"
        const val ATTACHMENT_BASE64 = "ZmlsZTI="
        const val ALT_MUNICIPALITY_NAME = "Porsgrunn 2"
        const val ALT_CASE_TYPE = "Graving 2"
        const val ALT_LOCATION_REFERENCE = "Ref-1 2"
        const val ALT_JOURNAL_DATE = "2024-01-05"
        const val ALT_DOCUMENT_TYPE = "Brev"
        const val ALT_CASE_HANDLER = "Handler 2"
    }
}
