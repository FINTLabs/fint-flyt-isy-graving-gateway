package no.novari.flyt.isygraving.gateway.instance.model

import jakarta.validation.constraints.NotBlank

data class JournalPostInstance(
    @field:NotBlank val archiveCaseId: String,
    @field:NotBlank val journalEntries: List<JournalEntry>,
    @field:NotBlank val tenant: String,
    @field:NotBlank val caseId: String,
    @field:NotBlank val caseArchiveGuid: String,
    @field:NotBlank val municipalityName: String,
    @field:NotBlank val caseType: String,
    @field:NotBlank val locationReference: String,
    @field:NotBlank val caseDate: String,
    @field:NotBlank val caseResponsible: String,
    @field:NotBlank val status: String,
    @field:NotBlank val callback: String,
)

data class JournalEntry(
    @field:NotBlank val municipalityName: String,
    @field:NotBlank val caseType: String,
    @field:NotBlank val locationReference: String,
    @field:NotBlank val date: String,
    @field:NotBlank val documentType: String,
    @field:NotBlank val caseHandler: String,
    @field:NotBlank val recipients: List<Recipient>,
    @field:NotBlank val documents: List<Document>,
)

data class Recipient(
    @field:NotBlank val name: String,
    @field:NotBlank val address: String,
    @field:NotBlank val postalCode: String,
    @field:NotBlank val organizationNumber: String,
)

data class Document(
    @field:NotBlank val title: String,
    @field:NotBlank val fileName: String,
    @field:NotBlank val mainDocument: Boolean,
    @field:NotBlank val lastModified: String,
    @field:NotBlank val status: String,
    @field:NotBlank val mediaType: String,
    @field:NotBlank val documentBase64: String,
)
