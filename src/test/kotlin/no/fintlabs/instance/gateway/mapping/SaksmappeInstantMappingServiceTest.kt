package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.model.Journalenhet
import no.fintlabs.instance.gateway.model.Klasse
import no.fintlabs.instance.gateway.model.Klassifikasjonssystem
import no.fintlabs.instance.gateway.model.ReferanseArkivdel
import no.fintlabs.instance.gateway.model.ReferanseEksternNoekkel
import no.fintlabs.instance.gateway.model.Saksmappe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.UUID

class SaksmappeInstantMappingServiceTest {
    private val service = SaksmappeInstantMappingService()

    @Test
    fun `map should populate valuePerKey and klasser collection`() {
        val saksmappe =
            sampleSaksmappe(
                klasser =
                    listOf(
                        klasse(
                            rekkefoelge = 1,
                            klasseID = "A1",
                            skjermet = false,
                            tittel = "Klasse A",
                            ledetekst = "Ledetekst A",
                            klassifikasjonssystemKodebeskrivelse = "Klasse A",
                        ),
                        klasse(
                            rekkefoelge = 2,
                            klasseID = "B2",
                            skjermet = true,
                            tittel = "Klasse B",
                            ledetekst = "Ledetekst B",
                            klassifikasjonssystemKodebeskrivelse = "Klasse B",
                        ),
                    ),
            )

        val persistFile: (File) -> UUID = { _ -> UUID.randomUUID() }

        val instanceObject: InstanceObject =
            service.map(
                sourceApplicationId = 7L,
                incomingInstance = saksmappe,
                persistFile = persistFile,
            )

        with(instanceObject.valuePerKey) {
            assertEquals("SYS-123", this["sysId"])
            assertEquals("Tittel på saken", this["tittel"])
            assertEquals("Offentlig tittel", this["offentligTittel"])
            assertEquals("2025-08-14", this["saksDato"])
            assertEquals("Skjermet tittel", this["skjermetTittel"])
            assertEquals("ADM-ENHET-01", this["administrativEnhet"])
            assertEquals("2026-01-01", this["kassasjonsDato"])
            assertEquals("AB", this["saksansvarligInit"])
            assertEquals("Tilgangsgruppe X", this["tilgangsgruppeNavn"])

            assertEquals("JE1", this["journalenhetKodeverdi"])
            assertEquals("Journalenhet 1", this["journalenhetKodebeskrivelse"])
            assertEquals("true", this["journalenhetErGyldig"])

            assertEquals("ARK-01", this["referanseArkivdelKodeverdi"])
            assertEquals("false", this["referanseArkivdelErGyldig"])

            assertEquals("ext-123", this["referanseEksternNoekkelNoekkel"])
            assertEquals("Fagsystem", this["referanseEksternNoekkelFagsystem"])
        }

        with(instanceObject.objectCollectionPerKey) {
            assertNotNull(this["klasser"], "Expected 'klasser' object collection to exist")
            val klasser = this["klasser"]!!.toList()
            assertEquals(2, klasser.size)

            with(klasser[0].valuePerKey) {
                assertEquals("1", this["rekkefoelge"])
                assertEquals("A1", this["klasse_id"])
                assertEquals("false", this["skjermetKlasse"])
                assertEquals("Klasse A", this["tittel"])
                assertEquals("Ledetekst A", this["ledetekst"])
                assertEquals("Klasse A", this["klassifikasjonssystemKodebeskrivelse"])
            }

            with(klasser[1].valuePerKey) {
                assertEquals("2", this["rekkefoelge"])
                assertEquals("B2", this["klasse_id"])
                assertEquals("true", this["skjermetKlasse"])
                assertEquals("Klasse B", this["tittel"])
                assertEquals("Ledetekst B", this["ledetekst"])
                assertEquals("Klasse B", this["klassifikasjonssystemKodebeskrivelse"])
            }
        }
    }

    private fun sampleSaksmappe(klasser: List<Klasse>): Saksmappe {
        return Saksmappe(
            sysId = "SYS-123",
            tittel = "Tittel på saken",
            offentligTittel = "Offentlig tittel",
            saksdato = "2025-08-14",
            skjermetTittel = "Skjermet tittel",
            administrativEnhet = "ADM-ENHET-01",
            kassasjonsdato = "2026-01-01",
            saksansvarligInit = "AB",
            tilgangsgruppeNavn = "Tilgangsgruppe X",
            journalenhet =
                Journalenhet(
                    kodeverdi = "JE1",
                    kodebeskrivelse = "Journalenhet 1",
                    erGyldig = true,
                ),
            referanseArkivdel =
                ReferanseArkivdel(
                    kodeverdi = "ARK-01",
                    erGyldig = false,
                ),
            referanseEksternNoekkel =
                ReferanseEksternNoekkel(
                    noekkel = "ext-123",
                    fagsystem = "Fagsystem",
                ),
            klasse = klasser,
        )
    }

    private fun klasse(
        rekkefoelge: Int,
        klasseID: String,
        skjermet: Boolean,
        tittel: String,
        ledetekst: String,
        klassifikasjonssystemKodebeskrivelse: String,
    ): Klasse {
        return Klasse(
            rekkefoelge = rekkefoelge,
            klasseID = klasseID,
            skjermetKlasse = skjermet,
            tittel = tittel,
            ledetekst = ledetekst,
            klassifikasjonssystem =
                Klassifikasjonssystem(
                    kodebeskrivelse = klassifikasjonssystemKodebeskrivelse,
                    kodeverdi = "", // TODO
                    erGyldig = true,
                ),
        )
    }
}
