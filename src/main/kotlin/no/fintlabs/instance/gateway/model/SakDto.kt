package no.fintlabs.instance.gateway.model

data class Sak(
    val saksId: String,
    val kommunenavn: String,
    val sakstype: String,
    val stedsreferanse: String,
    val saksdato: String,
    val saksansvarlig: String,
    val journalposter: List<Journalpost>,
)

data class Journalpost(
    val kommunenavn: String,
    val sakstype: String,
    val stedsreferanse: String,
    val dato: String,
    val dokumenttype: String,
    val saksbehandler: String,
    val mottakere: List<Mottaker>,
    val dokumenter: List<Dokument>,
)

data class Mottaker(
    val navn: String,
    val adresse: String,
    val postnummer: String,
    val orgNr: String,
)

data class Dokument(
    val tittel: String,
    val filnavn: String,
    val hoveddokument: Boolean,
    val mediatype: String,
    val documentBase64: String,
)
