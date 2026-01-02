package no.novari.flyt.isygraving.gateway.dispatch

data class CaseDispatchPayload(
    val tenant: String,
    val caseId: String,
    val caseArchiveGuid: String,
    val archiveCaseId: String,
)
