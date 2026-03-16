package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class CaseInstanceMappingServiceTest {
    private val mappingService = CaseInstanceMappingService()

    @Test
    fun `maps case instance fields`() {
        val instance = buildCaseInstance()

        val result =
            mappingService.map(
                sourceApplicationId = 7L,
                incomingInstance = instance,
            ) { _ -> UUID.randomUUID() }

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
        assertFalse(result.valuePerKey.containsKey("callback"))
    }

    private fun buildCaseInstance(): CaseInstance =
        CaseInstance(
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
        )

    private companion object {
        const val TENANT = "more_romsdal"
        const val CASE_ID = "GT_20260227_1715"
        const val CASE_TYPE = "Gravetillatelse"
        const val BUSINESS_AREA = "Gravetillatelse"
        const val BUSINESS_AREA_TYPE = "Graving i veibanen "
        const val CASE_ARCHIVE_GUID = "d1aa088b-f3a7-438f-bf4f-be6cc74ac5d6"
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
        const val CALLBACK = "https://localhost:5001/Case/Public/SetArchiveCaseReference"
    }
}
