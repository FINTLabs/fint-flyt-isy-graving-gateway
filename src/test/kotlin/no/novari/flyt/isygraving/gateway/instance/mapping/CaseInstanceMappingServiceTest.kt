package no.novari.flyt.isygraving.gateway.instance.mapping

import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

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
        assertEquals(MUNICIPALITY_NAME, result.valuePerKey["municipalityName"])
        assertEquals(CASE_TYPE, result.valuePerKey["caseType"])
        assertEquals(LOCATION_REFERENCE, result.valuePerKey["locationReference"])
        assertEquals(CASE_DATE, result.valuePerKey["caseDate"])
        assertEquals(CASE_RESPONSIBLE, result.valuePerKey["caseResponsible"])
        assertEquals(STATUS, result.valuePerKey["status"])
        assertEquals(CALLBACK, result.valuePerKey["callback"])
    }

    private fun buildCaseInstance(): CaseInstance =
        CaseInstance(
            caseId = CASE_ID,
            caseArchiveGuid = CASE_ARCHIVE_GUID,
            tenant = TENANT,
            municipalityName = MUNICIPALITY_NAME,
            caseType = CASE_TYPE,
            locationReference = LOCATION_REFERENCE,
            caseDate = CASE_DATE,
            caseResponsible = CASE_RESPONSIBLE,
            status = STATUS,
            callback = CALLBACK,
        )

    private companion object {
        const val CASE_ID = "case-1"
        const val CASE_ARCHIVE_GUID = "guid-123"
        const val TENANT = "tenant-a"
        const val MUNICIPALITY_NAME = "Porsgrunn"
        const val CASE_TYPE = "Graving"
        const val LOCATION_REFERENCE = "Ref-1"
        const val CASE_DATE = "2024-01-01"
        const val CASE_RESPONSIBLE = "Responsible"
        const val STATUS = "OPEN"
        const val CALLBACK = "https://callback"
    }
}
