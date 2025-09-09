package no.fintlabs.instance.gateway.mapping

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.JOURNALPOST
import no.fintlabs.instance.gateway.mapping.InstanceKeys.KOMMUNENAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSANSVARLIG
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSDATO
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSTYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKS_ID
import no.fintlabs.instance.gateway.mapping.InstanceKeys.STEDSREFERANSE
import no.fintlabs.instance.gateway.model.Journalpost
import no.fintlabs.instance.gateway.model.Sak
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(MockKExtension::class)
class SakMappingServiceTest {
    @MockK
    private lateinit var journalpostMappingService: JournalpostMappingService

    private lateinit var sakMappingService: SakMappingService

    @BeforeEach
    fun setup() {
        sakMappingService = SakMappingService(journalpostMappingService)
    }

    @Test
    fun `maps top-level fields and handles empty journalposter`() {
        val sourceApplicationId = 42L
        val sak =
            sak(
                saksId = "S-123",
                kommunenavn = "Fjordvik",
                sakstype = "Graving",
                stedsreferanse = "Gnr 12 Bnr 34",
                saksdato = "2025-09-05",
                saksansvarlig = "Ola Nordmann",
                journalposter = emptyList(),
            )
        val persistFile: (File) -> UUID = { UUID.randomUUID() }

        val result = sakMappingService.map(sourceApplicationId, sak, persistFile)

        assertEquals(sak.saksId, result.valuePerKey[SAKS_ID])
        assertEquals(sak.kommunenavn, result.valuePerKey[KOMMUNENAVN])
        assertEquals(sak.sakstype, result.valuePerKey[SAKSTYPE])
        assertEquals(sak.stedsreferanse, result.valuePerKey[STEDSREFERANSE])
        assertEquals(sak.saksdato, result.valuePerKey[SAKSDATO])
        assertEquals(sak.saksansvarlig, result.valuePerKey[SAKSANSVARLIG])

        val journalpostObjects = result.objectCollectionPerKey[JOURNALPOST]
        assertTrue(journalpostObjects == null || journalpostObjects.isEmpty(), "Expected no journalpost objects")
    }

    @Test
    fun `delegates each journalpost to JournalpostMappingService and includes results`() {
        val sourceApplicationId = 7L
        val saksId = "S-999"
        val jp1: Journalpost = mockk(relaxed = true)
        val jp2: Journalpost = mockk(relaxed = true)

        val sak =
            sak(
                saksId = saksId,
                kommunenavn = "TestKommune",
                sakstype = "TypeA",
                stedsreferanse = "Ref-1",
                saksdato = "2025-09-01",
                saksansvarlig = "ola.nordmann",
                journalposter = listOf(jp1, jp2),
            )

        val mapped1 = InstanceObject(valuePerKey = mapOf("id" to "jp1"))
        val mapped2 = InstanceObject(valuePerKey = mapOf("id" to "jp2"))

        every {
            journalpostMappingService.map(
                sourceApplicationId = sourceApplicationId,
                saksId = saksId,
                persistFile = any(),
                journalpost = jp1,
            )
        } returns mapped1

        every {
            journalpostMappingService.map(
                sourceApplicationId = sourceApplicationId,
                saksId = saksId,
                persistFile = any(),
                journalpost = jp2,
            )
        } returns mapped2

        val persistFile: (File) -> UUID = { UUID.randomUUID() }

        val result = sakMappingService.map(sourceApplicationId, sak, persistFile)

        val journalpostObjects = result.objectCollectionPerKey[JOURNALPOST]
        requireNotNull(journalpostObjects) { "Expected JOURNALPOST list to be present" }
        val ids = journalpostObjects.map { it.valuePerKey["id"] }
        assertEquals(listOf("jp1", "jp2"), ids)

        verify {
            journalpostMappingService.map(sourceApplicationId, saksId, any(), jp1)
        }
        verify {
            journalpostMappingService.map(sourceApplicationId, saksId, any(), jp2)
        }
    }

    private fun sak(
        saksId: String,
        kommunenavn: String,
        sakstype: String,
        stedsreferanse: String,
        saksdato: String,
        saksansvarlig: String,
        journalposter: List<Journalpost>,
    ): Sak =
        Sak(
            saksId = saksId,
            kommunenavn = kommunenavn,
            sakstype = sakstype,
            stedsreferanse = stedsreferanse,
            saksdato = saksdato,
            saksansvarlig = saksansvarlig,
            journalposter = journalposter,
        )
}
