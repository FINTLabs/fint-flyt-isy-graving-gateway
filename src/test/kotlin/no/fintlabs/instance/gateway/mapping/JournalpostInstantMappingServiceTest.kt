package no.fintlabs.instance.gateway.mapping

import java.util.UUID
import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.instance.gateway.model.Avskrivning
import no.fintlabs.instance.gateway.model.Dokument
import no.fintlabs.instance.gateway.model.Filinnhold
import no.fintlabs.instance.gateway.model.Journalpost
import no.fintlabs.instance.gateway.model.KodeverdiGyldig
import no.fintlabs.instance.gateway.model.Kontakt
import no.fintlabs.instance.gateway.model.Korrespondansepart
import no.fintlabs.instance.gateway.model.ReferanseEksternNoekkel
import no.fintlabs.instance.gateway.model.Saksnr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class JournalpostInstantMappingServiceTest {

    private val service = JournalpostInstantMappingService()

    @Test
    fun `map should build InstanceObject with values and nested collections and persist files`() {
        val persistedFiles = mutableListOf<File>()
        val fixedUuid = UUID.randomUUID()
        val persistFile: (File) -> UUID = { f ->
            persistedFiles.add(f)
            fixedUuid
        }

        val journalpost = sampleJournalpost(
            dokumenter = listOf(
                dokument(
                    filnavn = "vedlegg-1.pdf",
                    base64 = "fake-pdf".toByteArray(),
                ),
                dokument(
                    filnavn = "bilde-1.png",
                    base64 = "fake-png".toByteArray(),
                )
            )
        )

        val instanceObject = service.map(
            sourceApplicationId = 7L,
            incomingInstance = journalpost,
            persistFile = persistFile
        )

        with(instanceObject.valuePerKey) {
            assertEquals(journalpost.journaldato, this["journalDato"])
            assertEquals(journalpost.journalposttype.kodeverdi, this["journalposttypeKodeverdi"])
            assertEquals(journalpost.journalposttype.erGyldig.toString(), this["journalposttypeErGyldig"])
            assertEquals(journalpost.dokumentetsDato, this["dokumentetsDato"])
            assertEquals(journalpost.journalstatus.kodeverdi, this["journalstatusKodeverdi"])
            assertEquals(journalpost.journalstatus.erGyldig.toString(), this["journalstatusErGyldig"])
            assertEquals(journalpost.tittel, this["tittel"])
            assertEquals(journalpost.skjermetTittel.toString(), this["skjermetTittel"])
            assertEquals(journalpost.forfallsdato, this["forfallsdato"])
            assertEquals(journalpost.saksnr.saksaar.toString(), this["saksnrSaksaar"])
            assertEquals(journalpost.saksnr.sakssekvensnummer.toString(), this["saksnrSakssekvensnummer"])
            assertEquals(journalpost.referanseEksternNoekkel.noekkel, this["referanseEksternNoekkelNoekkel"])
            assertEquals(journalpost.referanseEksternNoekkel.fagsystem, this["referanseEksternNoekkelFagsystem"])
        }

        with(instanceObject.objectCollectionPerKey) {
            assertNotNull(get("korrespondansepart"))
            assertNotNull(get("referanseAvskrivninger"))
            assertNotNull(get("dokumenter"))

            with(this["dokumenter"]!!.toList()) {
                assertEquals(MediaType.APPLICATION_PDF_VALUE, this[0].valuePerKey["mediatype"])
                assertEquals(MediaType.IMAGE_PNG_VALUE, this[1].valuePerKey["mediatype"])
                assertEquals(fixedUuid.toString(), this[0].valuePerKey["fil"])
                assertEquals(fixedUuid.toString(), this[1].valuePerKey["fil"])
            }
        }

        assertThat(persistedFiles).hasSize(2)
        assertEquals("vedlegg-1.pdf", persistedFiles[0].name)
        assertEquals(MediaType.APPLICATION_PDF, persistedFiles[0].type)
        assertEquals("bilde-1.png", persistedFiles[1].name)
        assertEquals(MediaType.IMAGE_PNG, persistedFiles[1].type)
    }

    @Test
    fun `map should throw for unknown media type`() {
        val persistFile: (File) -> UUID = { _ -> UUID.randomUUID() }
        val journalpost = sampleJournalpost(
            dokumenter = listOf(
                dokument(filnavn = "unknown.foobar", base64 = "fake".toByteArray()),
            )
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.map(1L, journalpost, persistFile)
        }

        assertThat(exception.message).contains("No media type found for fileName=unknown.foobar")
    }

    private fun dokument(filnavn: String, base64: ByteArray): Dokument {
        return Dokument(
            fil = Filinnhold(
                filnavn = filnavn,
                base64 = base64,
                mimeType = MediaType.APPLICATION_PDF_VALUE
            ),
            tilknyttetRegistreringSom = KodeverdiGyldig("", true),
            tittel = filnavn,
            dokumentstatus = KodeverdiGyldig("", true),
            variantformat = KodeverdiGyldig("", true),
            referanseJournalpostSystemID = 42L
        )
    }

    private fun sampleJournalpost(
        dokumenter: List<Dokument>
    ): Journalpost {
        return Journalpost(
            journaldato = "2025-08-14",
            journalposttype = KodeverdiGyldig("", true), // TODO
            dokumentetsDato = "2025-08-13",
            journalstatus = KodeverdiGyldig("", true), // TODO
            tittel = "Tittel",
            skjermetTittel = false,
            forfallsdato = "2025-08-20",
            saksnr = Saksnr(saksaar = 2025, sakssekvensnummer = 123),
            referanseEksternNoekkel = ReferanseEksternNoekkel(
                noekkel = UUID.randomUUID().toString(),
                fagsystem = "Fagsystem"
            ),
            korrespondansepart = listOf(
                Korrespondansepart(
                    korrespondanseparttype = KodeverdiGyldig("", true), // TODO
                    skjermetKorrespondansepart = false,
                    fristBesvarelse = "",
                    kontakt = Kontakt(
                        navn = "Navn AS",
                        organisasjonsnummer = "123456789"
                    )
                )
            ),
            referanseAvskrivninger = listOf(
                Avskrivning(
                    avskrivningsdato = "2025-08-21",
                    avskrivningsmaate = KodeverdiGyldig("", true) // TODO
                )
            ),
            dokumenter = dokumenter
        )
    }
}