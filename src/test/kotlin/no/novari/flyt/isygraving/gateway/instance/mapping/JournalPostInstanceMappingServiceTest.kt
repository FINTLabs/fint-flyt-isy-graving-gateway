package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.isygraving.gateway.instance.model.Document
import no.novari.flyt.isygraving.gateway.instance.model.JournalEntry
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import no.novari.flyt.isygraving.gateway.instance.model.Recipient
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class JournalPostInstanceMappingServiceTest {
    private val mappingService = JournalPostInstanceMappingService()

    @Test
    fun `maps journal post instance with multiple journal entries`() {
        val instance = buildJournalPostInstance()
        val fileIds =
            listOf(
                UUID.fromString("b1c2c31d-6b29-4cbe-b44b-7e42b1c7a2af"),
                UUID.fromString("7a1e57bb-1de0-4a45-8bb0-7d4aa9f75f7a"),
                UUID.fromString("5c645800-5f64-40c0-9910-f3cd6f2b7b09"),
                UUID.fromString("e8349401-6420-4adb-9f16-5f23c8c973d7"),
            )
        var fileIndex = 0

        val result =
            mappingService.map(
                sourceApplicationId = 7L,
                incomingInstance = instance,
            ) { _ -> fileIds[fileIndex++] }

        assertEquals(ARCHIVE_CASE_ID, result.valuePerKey["archiveCaseId"])
        assertEquals(TENANT, result.valuePerKey["tenant"])
        assertEquals(CASE_ID, result.valuePerKey["caseId"])
        assertEquals(CASE_TYPE, result.valuePerKey["caseType"])
        assertEquals(BUSINESS_AREA, result.valuePerKey["businessArea"])
        assertEquals(BUSINESS_AREA_TYPE, result.valuePerKey["businessAreaType"])
        assertEquals(CASE_ARCHIVE_GUID, result.valuePerKey["caseArchiveGuid"])
        assertEquals(MUNICIPALITY_NAME, result.valuePerKey["municipalityName"])
        assertEquals(LOCATION_REFERENCE, result.valuePerKey["locationReference"])
        assertEquals(LOCATION_REFERENCE_FULL, result.valuePerKey["locationReferenceFull"])
        assertEquals(LOCATION_REFERENCE_FORMATTED, result.valuePerKey["locationReferenceFormatted"])
        assertEquals(STREET_NAME, result.valuePerKey["streetName"])
        assertEquals(CASE_DATE, result.valuePerKey["caseDate"])
        assertEquals(CASE_YEAR, result.valuePerKey["caseYear"])
        assertEquals(CASE_RESPONSIBLE, result.valuePerKey["caseResponsible"])
        assertEquals(STATUS, result.valuePerKey["status"])
        assertEquals(STATUS_NAME, result.valuePerKey["statusName"])
        assertEquals(ARCHIVE_CASE_ID, result.valuePerKey["archiveCaseId"])
        assertFalse(result.valuePerKey.containsKey("callback"))
        assertFalse(result.valuePerKey.containsKey("documentType"))
        assertFalse(result.valuePerKey.containsKey("mainDocumentTitle"))

        val recipient =
            result.objectCollectionPerKey["recipients"]?.toList()?.single()
                ?: error("Missing recipients")
        assertEquals(RECIPIENT_NAME, recipient.valuePerKey["name"])
        assertEquals(RECIPIENT_ADDRESS, recipient.valuePerKey["address"])
        assertEquals(RECIPIENT_POSTAL_CODE, recipient.valuePerKey["postalCode"])
        assertEquals(RECIPIENT_CITY, recipient.valuePerKey["city"])
        assertEquals(RECIPIENT_ORG_NUMBER, recipient.valuePerKey["organizationNumber"])

        val journalEntries =
            result.objectCollectionPerKey["journalEntries"]?.toList()
                ?: error("Missing journalEntries")
        assertEquals(2, journalEntries.size)

        val firstJournalEntry = journalEntries.first()
        assertEquals(JOURNAL_DOCUMENT_TYPE, firstJournalEntry.valuePerKey["documentType"])
        assertEquals(DOCUMENT_TITLE, firstJournalEntry.valuePerKey["mainDocumentTitle"])
        assertEquals(DOCUMENT_FILE_NAME, firstJournalEntry.valuePerKey["mainDocumentFileName"])
        assertEquals(DOCUMENT_TAGS.joinToString(","), firstJournalEntry.valuePerKey["mainDocumentTags"])
        assertEquals(DOCUMENT_LAST_MODIFIED, firstJournalEntry.valuePerKey["mainDocumentLastModified"])
        assertEquals(DOCUMENT_STATUS, firstJournalEntry.valuePerKey["mainDocumentStatus"])
        assertEquals(DOCUMENT_MEDIA_TYPE, firstJournalEntry.valuePerKey["mainDocumentMediaType"])
        assertEquals(fileIds[0].toString(), firstJournalEntry.valuePerKey["mainDocumentBase64"])

        val firstAttachment =
            firstJournalEntry.objectCollectionPerKey["attachments"]?.toList()?.single()
                ?: error("Missing first attachments")
        assertEquals(ATTACHMENT_TITLE, firstAttachment.valuePerKey["title"])
        assertEquals(ATTACHMENT_FILE_NAME, firstAttachment.valuePerKey["fileName"])
        assertEquals(ATTACHMENT_TAGS.joinToString(","), firstAttachment.valuePerKey["tags"])
        assertEquals(ATTACHMENT_LAST_MODIFIED, firstAttachment.valuePerKey["lastModified"])
        assertEquals(ATTACHMENT_STATUS, firstAttachment.valuePerKey["status"])
        assertEquals(ATTACHMENT_MEDIA_TYPE, firstAttachment.valuePerKey["mediaType"])
        assertEquals(fileIds[1].toString(), firstAttachment.valuePerKey["documentBase64"])

        val secondJournalEntry = journalEntries.last()
        assertEquals(SECOND_JOURNAL_DOCUMENT_TYPE, secondJournalEntry.valuePerKey["documentType"])
        assertEquals(SECOND_DOCUMENT_TITLE, secondJournalEntry.valuePerKey["mainDocumentTitle"])
        assertEquals(SECOND_DOCUMENT_FILE_NAME, secondJournalEntry.valuePerKey["mainDocumentFileName"])
        assertEquals(SECOND_DOCUMENT_TAGS.joinToString(","), secondJournalEntry.valuePerKey["mainDocumentTags"])
        assertEquals(SECOND_DOCUMENT_LAST_MODIFIED, secondJournalEntry.valuePerKey["mainDocumentLastModified"])
        assertEquals(SECOND_DOCUMENT_STATUS, secondJournalEntry.valuePerKey["mainDocumentStatus"])
        assertEquals(SECOND_DOCUMENT_MEDIA_TYPE, secondJournalEntry.valuePerKey["mainDocumentMediaType"])
        assertEquals(fileIds[2].toString(), secondJournalEntry.valuePerKey["mainDocumentBase64"])

        val secondAttachment =
            secondJournalEntry.objectCollectionPerKey["attachments"]?.toList()?.single()
                ?: error("Missing second attachments")
        assertEquals(SECOND_ATTACHMENT_TITLE, secondAttachment.valuePerKey["title"])
        assertEquals(SECOND_ATTACHMENT_FILE_NAME, secondAttachment.valuePerKey["fileName"])
        assertEquals(SECOND_ATTACHMENT_TAGS.joinToString(","), secondAttachment.valuePerKey["tags"])
        assertEquals(SECOND_ATTACHMENT_LAST_MODIFIED, secondAttachment.valuePerKey["lastModified"])
        assertEquals(SECOND_ATTACHMENT_STATUS, secondAttachment.valuePerKey["status"])
        assertEquals(SECOND_ATTACHMENT_MEDIA_TYPE, secondAttachment.valuePerKey["mediaType"])
        assertEquals(fileIds[3].toString(), secondAttachment.valuePerKey["documentBase64"])
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
            tenant = TENANT,
            caseId = CASE_ID,
            caseType = CASE_TYPE,
            businessArea = BUSINESS_AREA,
            businessAreaType = BUSINESS_AREA_TYPE,
            caseArchiveGuid = CASE_ARCHIVE_GUID,
            municipalityName = MUNICIPALITY_NAME,
            locationReference = LOCATION_REFERENCE,
            locationReferenceFull = LOCATION_REFERENCE_FULL,
            locationReferenceFormatted = LOCATION_REFERENCE_FORMATTED,
            streetName = STREET_NAME,
            caseDate = CASE_DATE,
            caseYear = CASE_YEAR,
            caseResponsible = CASE_RESPONSIBLE,
            status = STATUS,
            statusName = STATUS_NAME,
            callback = CALLBACK,
            archiveCaseId = ARCHIVE_CASE_ID,
            recipients =
                listOf(
                    buildRecipient(),
                ),
            journalEntries =
                listOf(
                    buildJournalEntry(),
                    buildSecondJournalEntry(),
                ),
        )

    private fun buildJournalEntry(): JournalEntry =
        JournalEntry(
            documentType = JOURNAL_DOCUMENT_TYPE,
            documents = listOf(buildMainDocument(), buildAttachmentDocument()),
        )

    private fun buildSecondJournalEntry(): JournalEntry =
        JournalEntry(
            documentType = SECOND_JOURNAL_DOCUMENT_TYPE,
            documents = listOf(buildSecondMainDocument(), buildSecondAttachmentDocument()),
        )

    private fun buildRecipient(): Recipient =
        Recipient(
            name = RECIPIENT_NAME,
            address = RECIPIENT_ADDRESS,
            postalCode = RECIPIENT_POSTAL_CODE,
            city = RECIPIENT_CITY,
            organizationNumber = RECIPIENT_ORG_NUMBER,
        )

    private fun buildMainDocument(): Document =
        Document(
            title = DOCUMENT_TITLE,
            fileName = DOCUMENT_FILE_NAME,
            tags = DOCUMENT_TAGS,
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
            tags = ATTACHMENT_TAGS,
            mainDocument = false,
            lastModified = ATTACHMENT_LAST_MODIFIED,
            status = ATTACHMENT_STATUS,
            mediaType = ATTACHMENT_MEDIA_TYPE,
            documentBase64 = ATTACHMENT_BASE64,
        )

    private fun buildSecondMainDocument(): Document =
        Document(
            title = SECOND_DOCUMENT_TITLE,
            fileName = SECOND_DOCUMENT_FILE_NAME,
            tags = SECOND_DOCUMENT_TAGS,
            mainDocument = true,
            lastModified = SECOND_DOCUMENT_LAST_MODIFIED,
            status = SECOND_DOCUMENT_STATUS,
            mediaType = SECOND_DOCUMENT_MEDIA_TYPE,
            documentBase64 = SECOND_DOCUMENT_BASE64,
        )

    private fun buildSecondAttachmentDocument(): Document =
        Document(
            title = SECOND_ATTACHMENT_TITLE,
            fileName = SECOND_ATTACHMENT_FILE_NAME,
            tags = SECOND_ATTACHMENT_TAGS,
            mainDocument = false,
            lastModified = SECOND_ATTACHMENT_LAST_MODIFIED,
            status = SECOND_ATTACHMENT_STATUS,
            mediaType = SECOND_ATTACHMENT_MEDIA_TYPE,
            documentBase64 = SECOND_ATTACHMENT_BASE64,
        )

    private companion object {
        const val TENANT = "more_romsdal"
        const val CASE_ID = "GT_20260227_1715"
        const val CASE_TYPE = "Gravetillatelse"
        const val BUSINESS_AREA = "Gravetillatelse"
        const val BUSINESS_AREA_TYPE = "Graving i veibanen "
        const val CASE_ARCHIVE_GUID = "d9c79683-81a0-45f8-8bab-4cc552c8c15c"
        const val MUNICIPALITY_NAME = "Birkenes"
        const val LOCATION_REFERENCE = "FV3750"
        const val LOCATION_REFERENCE_FULL = "FV3750 S1D1 m4435 "
        const val LOCATION_REFERENCE_FORMATTED = "Fv.3750"
        const val STREET_NAME = "Urdalsveien"
        const val CASE_DATE = "2026-03-13"
        const val CASE_YEAR = "2026"
        const val CASE_RESPONSIBLE = "admin@isycase.isy.se"
        const val STATUS = "status.beviljat"
        const val STATUS_NAME = "Innvilget"
        const val CALLBACK = "https://localhost:5001/Case/Public/SetArchiveJournalPostReference"
        const val ARCHIVE_CASE_ID = "2026/123"
        const val RECIPIENT_NAME = "AmiciviASta"
        const val RECIPIENT_ADDRESS = "Slielunden 31 F"
        const val RECIPIENT_POSTAL_CODE = "1523"
        const val RECIPIENT_CITY = "Moss"
        const val RECIPIENT_ORG_NUMBER = "998736080"
        const val JOURNAL_DOCUMENT_TYPE = "I"
        const val DOCUMENT_TITLE = "Arkivdokument"
        const val DOCUMENT_FILE_NAME = "2026-03-13 GT_20260227_1715.docx"
        val DOCUMENT_TAGS = listOf("Arkivdokument")
        const val DOCUMENT_LAST_MODIFIED = "2026-03-13"
        const val DOCUMENT_STATUS = "status.beviljat"
        const val DOCUMENT_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        const val DOCUMENT_BASE64 = "ZmlsZQ=="
        const val ATTACHMENT_TITLE = "Ok-bilde"
        const val ATTACHMENT_FILE_NAME = "Ok-bilde.png"
        val ATTACHMENT_TAGS = listOf("sosifiler-innmålingsdata")
        const val ATTACHMENT_LAST_MODIFIED = "2026-03-13"
        const val ATTACHMENT_STATUS = "status.beviljat"
        const val ATTACHMENT_MEDIA_TYPE = "image/png"
        const val ATTACHMENT_BASE64 = "ZmlsZTI="
        const val SECOND_JOURNAL_DOCUMENT_TYPE = "U"
        const val SECOND_DOCUMENT_TITLE = "Vedtaksbrev"
        const val SECOND_DOCUMENT_FILE_NAME = "2026-03-13 GT_20260227_1715-2.pdf"
        val SECOND_DOCUMENT_TAGS = listOf("Vedtaksbrev")
        const val SECOND_DOCUMENT_LAST_MODIFIED = "2026-03-14"
        const val SECOND_DOCUMENT_STATUS = "status.ferdig"
        const val SECOND_DOCUMENT_MEDIA_TYPE = "application/pdf"
        const val SECOND_DOCUMENT_BASE64 = "ZmlsZTM="
        const val SECOND_ATTACHMENT_TITLE = "Kartutsnitt"
        const val SECOND_ATTACHMENT_FILE_NAME = "kartutsnitt.pdf"
        val SECOND_ATTACHMENT_TAGS = listOf("kartvedlegg")
        const val SECOND_ATTACHMENT_LAST_MODIFIED = "2026-03-14"
        const val SECOND_ATTACHMENT_STATUS = "status.ferdig"
        const val SECOND_ATTACHMENT_MEDIA_TYPE = "application/pdf"
        const val SECOND_ATTACHMENT_BASE64 = "ZmlsZTQ="
    }
}
