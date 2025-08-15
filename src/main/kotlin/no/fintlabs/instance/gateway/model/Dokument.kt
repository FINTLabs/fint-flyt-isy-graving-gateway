package no.fintlabs.instance.gateway.model

data class Dokument(
    val tilknyttetRegistreringSom: KodeverdiGyldig,
    val tittel: String,
    val dokumentstatus: KodeverdiGyldig,
    val variantformat: KodeverdiGyldig,
    val referanseJournalpostSystemID: Long,
    val fil: Filinnhold
)

