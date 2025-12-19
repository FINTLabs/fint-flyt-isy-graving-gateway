package no.novari.flyt.isygraving.gateway.instance.model

import jakarta.validation.constraints.NotBlank

data class CaseInstance(
    @field:NotBlank val caseId: String,
//    @field:NotBlank val caseArchiveGuid: String,
    @field:NotBlank val tenant: String,
    @field:NotBlank val municipalityName: String,
    @field:NotBlank val caseType: String,
    @field:NotBlank val locationReference: String,
    @field:NotBlank val caseDate: String,
    @field:NotBlank val caseResponsible: String,
    @field:NotBlank val status: String,
    @field:NotBlank val date: String,
    @field:NotBlank val callback: String,
)
