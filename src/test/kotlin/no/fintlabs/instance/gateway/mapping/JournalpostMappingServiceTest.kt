package no.fintlabs.instance.gateway.mapping

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.datafaker.Faker
import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.DATO
import no.fintlabs.instance.gateway.mapping.InstanceKeys.DOKUMENTER
import no.fintlabs.instance.gateway.mapping.InstanceKeys.DOKUMENTTYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.KOMMUNENAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.MOTTAKERE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSBEHANDLER
import no.fintlabs.instance.gateway.mapping.InstanceKeys.SAKSTYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.STEDSREFERANSE
import no.fintlabs.instance.gateway.model.Dokument
import no.fintlabs.instance.gateway.model.Journalpost
import no.fintlabs.instance.gateway.model.Mottaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import java.util.Base64
import java.util.UUID

@ExtendWith(MockKExtension::class)
class JournalpostMappingServiceTest {
    private val faker = Faker()

    @MockK
    private lateinit var mottakerMappingService: MottakerMappingService

    @MockK
    private lateinit var dokumenterMappingService: DokumenterMappingService

    private lateinit var service: JournalpostMappingService

    @BeforeEach
    fun setup() {
        service = JournalpostMappingService(mottakerMappingService, dokumenterMappingService)
    }

    @Test
    fun `map builds InstanceObject with scalar values and mapped collections`() {
        val sourceApplicationId = 42L
        val saksId = "SAK-2025-0001"
        val persistFile: (File) -> UUID = { UUID.fromString("00000000-0000-0000-0000-000000000042") }

        val mottaker1 = buildMottaker()
        val mottaker2 = buildMottaker()

        val dokument1 = buildDokument()
        val dokument2 = buildDokument()

        val journalpost = buildJournalpost(listOf(mottaker1, mottaker2), listOf(dokument1, dokument2))

        val mappedMottaker1 = InstanceObject(valuePerKey = emptyMap(), objectCollectionPerKey = mutableMapOf())
        val mappedMottaker2 = InstanceObject(valuePerKey = mapOf("x" to "y"), objectCollectionPerKey = mutableMapOf())

        every { mottakerMappingService.map(mottaker1) } returns mappedMottaker1
        every { mottakerMappingService.map(mottaker2) } returns mappedMottaker2

        val mappedDok1 = InstanceObject(valuePerKey = mapOf("dok" to "1"), objectCollectionPerKey = mutableMapOf())
        val mappedDok2 = InstanceObject(valuePerKey = mapOf("dok" to "2"), objectCollectionPerKey = mutableMapOf())

        every {
            dokumenterMappingService.map(
                persistFile = any(),
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = saksId,
                dokumenter = any(),
            )
        } returns listOf(mappedDok1, mappedDok2)

        val result =
            service.map(
                sourceApplicationId = sourceApplicationId,
                saksId = saksId,
                persistFile = persistFile,
                journalpost = journalpost,
            )

        assertThat(result.valuePerKey)
            .containsEntry(KOMMUNENAVN, journalpost.kommunenavn)
            .containsEntry(SAKSTYPE, journalpost.sakstype)
            .containsEntry(STEDSREFERANSE, journalpost.stedsreferanse)
            .containsEntry(DATO, journalpost.dato)
            .containsEntry(DOKUMENTTYPE, journalpost.dokumenttype)
            .containsEntry(SAKSBEHANDLER, journalpost.saksbehandler)

        val objectCollections = result.objectCollectionPerKey
        assertThat(objectCollections).containsKeys(MOTTAKERE, DOKUMENTER)

        assertThat(objectCollections[MOTTAKERE]).isNotNull
        assertThat(objectCollections[MOTTAKERE]).containsExactly(mappedMottaker1, mappedMottaker2)

        assertThat(objectCollections[DOKUMENTER]).isNotNull
        assertThat(objectCollections[DOKUMENTER]).containsExactly(mappedDok1, mappedDok2)

        verify { mottakerMappingService.map(mottaker1) }
        verify { mottakerMappingService.map(mottaker2) }

        val persistSlot = slot<(File) -> UUID>()
        val dokumenterSlot = slot<List<Dokument>>()

        verify {
            dokumenterMappingService.map(
                persistFile = capture(persistSlot),
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = saksId,
                dokumenter = capture(dokumenterSlot),
            )
        }

        val fakeFile = mockk<File>(relaxed = true)
        val uuidFromService = persistSlot.captured.invoke(fakeFile)
        val uuidDirect = persistFile.invoke(fakeFile)
        assertThat(uuidFromService).isEqualTo(uuidDirect)

        assertThat(dokumenterSlot.captured.size).isEqualTo(2)

        confirmVerified(mottakerMappingService, dokumenterMappingService)
    }

    private fun buildJournalpost(
        mottakere: List<Mottaker> = emptyList(),
        dokumenter: List<Dokument> = emptyList(),
    ): Journalpost {
        return Journalpost(
            kommunenavn = "Fjordvik",
            sakstype = "Innsyn",
            stedsreferanse = "Gnr 12/Bnr 34",
            dato = "2025-08-09",
            dokumenttype = "Søknad",
            saksbehandler = "Line Larsen",
            mottakere = mottakere,
            dokumenter = dokumenter,
        )
    }

    private fun buildMottaker(): Mottaker {
        return Mottaker(
            navn = faker.name().name(),
            adresse = faker.address().streetAddress(),
            postnummer = faker.address().zipCode(),
            orgNr = faker.number().randomNumber().toString(),
        )
    }

    private fun buildDokument(): Dokument {
        return Dokument(
            tittel = faker.book().title(),
            filnavn = faker.file().fileName(null, null, "pdf", null),
            hoveddokument = true,
            mediatype = MediaType.APPLICATION_PDF_VALUE,
            documentBase64 = Base64.getEncoder().encodeToString("PDF".toByteArray()),
        )
    }

    @Test
    fun `map tolerates empty collections`() {
        val journalpost = buildJournalpost()

        every { mottakerMappingService.map(any()) } answers { InstanceObject(emptyMap(), mutableMapOf()) }
        every {
            dokumenterMappingService.map(
                persistFile = any(),
                sourceApplicationId = any(),
                sourceApplicationInstanceId = any(),
                dokumenter = any(),
            )
        } returns emptyList()

        val result =
            service.map(
                sourceApplicationId = 1L,
                saksId = "S-0",
                persistFile = { UUID.randomUUID() },
                journalpost = journalpost,
            )

        assertThat(result.objectCollectionPerKey[MOTTAKERE]).isEmpty()
        assertThat(result.objectCollectionPerKey[DOKUMENTER]).isEmpty()

        verify(exactly = 0) { mottakerMappingService.map(any()) }
        verify {
            dokumenterMappingService.map(
                persistFile = any(),
                sourceApplicationId = 1L,
                sourceApplicationInstanceId = "S-0",
                dokumenter = emptyList(),
            )
        }
    }
}
