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

        assertEquals(CASE_ID, result.valuePerKey["caseId"])
        assertEquals(CASE_ARCHIVE_GUID, result.valuePerKey["caseArchiveGuid"])
        assertEquals(TENANT, result.valuePerKey["tenant"])
        assertEquals(MUNICIPALITY_NAME, result.valuePerKey["municipalityName"])
        assertEquals(CASE_TYPE, result.valuePerKey["caseType"])
        assertEquals(BUSINESS_AREA, result.valuePerKey["businessArea"])
        assertEquals(BUSINESS_AREA_TYPE, result.valuePerKey["businessAreaType"])
        assertEquals(LOCATION_REFERENCE, result.valuePerKey["locationReference"])
        assertEquals(LOCATION_REFERENCE_FORMATTED, result.valuePerKey["locationReferenceFormatted"])
        assertEquals(CASE_DATE, result.valuePerKey["caseDate"])
        assertEquals(CASE_YEAR, result.valuePerKey["caseYear"])
        assertEquals(CASE_RESPONSIBLE, result.valuePerKey["caseResponsible"])
        assertEquals(STATUS, result.valuePerKey["status"])
        assertEquals(STATUS_NAME, result.valuePerKey["statusName"])
        assertFalse(result.valuePerKey.containsKey("callback"))
    }

    private fun buildCaseInstance(): CaseInstance =
        CaseInstance(
            caseId = CASE_ID,
            caseArchiveGuid = CASE_ARCHIVE_GUID,
            tenant = TENANT,
            municipalityName = MUNICIPALITY_NAME,
            caseType = CASE_TYPE,
            businessArea = BUSINESS_AREA,
            businessAreaType = BUSINESS_AREA_TYPE,
            locationReference = LOCATION_REFERENCE,
            locationReferenceFormatted = LOCATION_REFERENCE_FORMATTED,
            caseDate = CASE_DATE,
            caseYear = CASE_YEAR,
            caseResponsible = CASE_RESPONSIBLE,
            status = STATUS,
            statusName = STATUS_NAME,
            callback = CALLBACK,
        )

    private companion object {
        const val CASE_ID = "case-1"
        const val CASE_ARCHIVE_GUID = "guid-123"
        const val TENANT = "tenant-a"
        const val MUNICIPALITY_NAME = "Porsgrunn"
        const val CASE_TYPE = "Graving"
        const val BUSINESS_AREA = "Road"
        const val BUSINESS_AREA_TYPE = "Maintenance"
        const val LOCATION_REFERENCE = "Ref-1"
        const val LOCATION_REFERENCE_FORMATTED = "Ref-1 formatted"
        const val CASE_DATE = "2024-01-01"
        const val CASE_YEAR = "2024"
        const val CASE_RESPONSIBLE = "Responsible"
        const val STATUS = "OPEN"
        const val STATUS_NAME = "Open"
        const val CALLBACK = "https://callback"
    }
}
