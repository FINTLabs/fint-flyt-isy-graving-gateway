package no.fintlabs.instance.gateway.mapping

import net.datafaker.Faker
import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.FILNAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.FIL_ID
import no.fintlabs.instance.gateway.mapping.InstanceKeys.HOVEDDOKUMENT
import no.fintlabs.instance.gateway.mapping.InstanceKeys.MEDIATYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.TITTEL
import no.fintlabs.instance.gateway.model.Dokument
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import java.util.Base64
import java.util.UUID

class DokumenterMappingServiceTest {
    private val faker: Faker = Faker()

    private val service = DokumenterMappingService()

    @Test
    fun `map maps each Dokument to InstanceObject and forwards correct File to persist`() {
        val sourceApplicationId = 42L
        val sourceApplicationInstanceId = "SAK-2025-0001"

        val dokumenter =
            listOf(
                Dokument(
                    tittel = faker.book().title(),
                    filnavn = faker.file().fileName(null, null, "pdf", null),
                    hoveddokument = false,
                    mediatype = MediaType.APPLICATION_PDF_VALUE,
                    documentBase64 = Base64.getEncoder().encodeToString("Vedlegg A content".toByteArray()),
                ),
                Dokument(
                    tittel = faker.book().title(),
                    filnavn = faker.file().fileName(null, null, "png", null),
                    hoveddokument = true,
                    mediatype = MediaType.IMAGE_PNG_VALUE,
                    documentBase64 = Base64.getEncoder().encodeToString("Bilde".toByteArray()),
                ),
            )

        val capturedFiles = mutableListOf<File>()
        val returnedIds =
            listOf(
                UUID.fromString("00000000-0000-0000-0000-0000000000A1"),
                UUID.fromString("00000000-0000-0000-0000-0000000000B2"),
            )
        val persistFile: (File) -> UUID = { f ->
            capturedFiles += f
            returnedIds[capturedFiles.size - 1]
        }

        val result: List<InstanceObject> =
            service.map(
                persistFile = persistFile,
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                dokumenter = dokumenter,
            )

        assertThat(capturedFiles).hasSize(2)

        assertThat(capturedFiles[0].name).isEqualTo(dokumenter[0].filnavn)
        assertThat(capturedFiles[0].type).isEqualTo(MediaType.APPLICATION_PDF)
        assertThat(capturedFiles[0].sourceApplicationId).isEqualTo(sourceApplicationId)
        assertThat(capturedFiles[0].sourceApplicationInstanceId).isEqualTo(sourceApplicationInstanceId)
        assertThat(capturedFiles[0].encoding).isEqualTo("UTF-8")
        assertThat(capturedFiles[0].base64Contents).isEqualTo(dokumenter[0].documentBase64)

        assertThat(capturedFiles[1].name).isEqualTo(dokumenter[1].filnavn)
        assertThat(capturedFiles[1].type).isEqualTo(MediaType.IMAGE_PNG)
        assertThat(capturedFiles[1].sourceApplicationId).isEqualTo(sourceApplicationId)
        assertThat(capturedFiles[1].sourceApplicationInstanceId).isEqualTo(sourceApplicationInstanceId)
        assertThat(capturedFiles[1].encoding).isEqualTo("UTF-8")
        assertThat(capturedFiles[1].base64Contents).isEqualTo(dokumenter[1].documentBase64)

        assertThat(result).hasSize(2)

        val m0 = result[0].valuePerKey
        assertThat(m0[TITTEL]).isEqualTo(dokumenter[0].tittel)
        assertThat(m0[FILNAVN]).isEqualTo(dokumenter[0].filnavn)
        assertThat(m0[HOVEDDOKUMENT].toBoolean()).isEqualTo(dokumenter[0].hoveddokument)
        assertThat(m0[MEDIATYPE]).isEqualTo(dokumenter[0].mediatype)
        assertThat(UUID.fromString(m0[FIL_ID])).isEqualTo(returnedIds[0])

        val m1 = result[1].valuePerKey
        assertThat(m1[TITTEL]).isEqualTo(dokumenter[1].tittel)
        assertThat(m1[FILNAVN]).isEqualTo(dokumenter[1].filnavn)
        assertThat(m1[HOVEDDOKUMENT].toBoolean()).isEqualTo(dokumenter[1].hoveddokument)
        assertThat(m1[MEDIATYPE]).isEqualTo(dokumenter[1].mediatype)
        assertThat(UUID.fromString((m1[FIL_ID]))).isEqualTo(returnedIds[1])
    }

    @Test
    fun `map throws when media type cannot be determined from filename`() {
        val dokumenter =
            listOf(
                Dokument(
                    tittel = "Ukjent",
                    filnavn = "fil-uten-endelse",
                    hoveddokument = false,
                    mediatype = MediaType.APPLICATION_PDF_VALUE,
                    documentBase64 = Base64.getEncoder().encodeToString("this is a virus".toByteArray()),
                ),
            )

        val persistFile: (File) -> UUID = { UUID.randomUUID() }

        assertThatThrownBy {
            service.map(
                persistFile = persistFile,
                sourceApplicationId = 1L,
                sourceApplicationInstanceId = "X",
                dokumenter = dokumenter,
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("fil-uten-endelse")
    }
}
