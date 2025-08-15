package no.fintlabs.instance.gateway.model

data class Journalpost(
    val journaldato: String,
    val journalposttype: KodeverdiGyldig,
    val dokumentetsDato: String,
    val journalstatus: KodeverdiGyldig,
    val tittel: String,
    val skjermetTittel: Boolean,
    val forfallsdato: String,
    val saksnr: Saksnr,
    val korrespondansepart: List<Korrespondansepart>,
    val referanseEksternNoekkel: ReferanseEksternNoekkel,
    val referanseAvskrivninger: List<Avskrivning>,
    val dokumenter: List<Dokument>
)